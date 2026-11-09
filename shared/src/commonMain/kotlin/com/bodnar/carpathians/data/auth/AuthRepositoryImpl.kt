package com.bodnar.carpathians.data.auth

import com.bodnar.carpathians.data.auth.remote.AuthApi
import com.bodnar.carpathians.data.auth.remote.RegisterRequestDto
import com.bodnar.carpathians.domain.auth.SignUpData
import com.bodnar.carpathians.data.auth.remote.clearCachedTokens
import com.bodnar.carpathians.data.auth.remote.toTokens
import com.bodnar.carpathians.data.auth.remote.toUser
import com.bodnar.carpathians.domain.auth.AuthError
import com.bodnar.carpathians.domain.auth.AuthRepository
import com.bodnar.carpathians.domain.auth.AuthResult
import com.bodnar.carpathians.domain.auth.TokenStorage
import com.bodnar.carpathians.domain.auth.User
import com.bodnar.carpathians.logging.AppLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerializationException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val client: HttpClient,
    private val tokenStorage: TokenStorage,
    private val sessionEvents: SessionEvents,
) : AuthRepository {

    private val log = AppLogger.withTag("AuthRepository")

    override val sessionExpired: Flow<Unit> = sessionEvents.expired

    override suspend fun login(username: String, password: String): AuthResult<User> = safeCall {
        val response = api.login(username, password)
        tokenStorage.saveTokens(response.toTokens())
        client.clearCachedTokens()
        log.i { "Logged in as ${response.username}" }
        response.toUser()
    }

    override suspend fun register(data: SignUpData): AuthResult<User> = safeCall {
        val user = api.register(
            RegisterRequestDto(data.firstName, data.username, data.email, data.password)
        ).toUser()
        log.i { "Registered ${user.username} (id ${user.id})" }
        user
    }

    override suspend fun getCurrentUser(): AuthResult<User> {
        if (tokenStorage.getTokens() == null) return AuthResult.Failure(AuthError.SessionExpired)
        return safeCall(isAuthorizedCall = true) { api.me().toUser() }
    }

    override suspend fun isLoggedIn(): Boolean = tokenStorage.getTokens() != null

    override suspend fun logout() {
        tokenStorage.clear()
        client.clearCachedTokens()
        log.i { "Logged out" }
    }

    /**
     * Виконує запит і перетворює винятки Ktor на [AuthError].
     * Для запитів з токеном 401 означає, що навіть після оновлення токенів доступу немає.
     */
    private suspend fun <T> safeCall(
        isAuthorizedCall: Boolean = false,
        block: suspend () -> T,
    ): AuthResult<T> = try {
        AuthResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        val error = e.toAuthError(isAuthorizedCall)
        log.w(e) { "Request failed: $error" }
        if (error == AuthError.SessionExpired) {
            tokenStorage.clear()
            client.clearCachedTokens()
        }
        AuthResult.Failure(error)
    }

    private fun Exception.toAuthError(isAuthorizedCall: Boolean): AuthError = when (this) {
        is ClientRequestException -> when {
            isAuthorizedCall && (response.status == HttpStatusCode.Unauthorized ||
                response.status == HttpStatusCode.Forbidden) -> AuthError.SessionExpired
            response.status == HttpStatusCode.BadRequest ||
                response.status == HttpStatusCode.Unauthorized ||
                response.status == HttpStatusCode.NotFound -> AuthError.InvalidCredentials
            else -> AuthError.Unknown
        }
        is ServerResponseException -> AuthError.Server
        is SerializationException -> AuthError.Unknown
        // Обрив зʼєднання, тайм-аут, відсутність мережі (IOException, помилка fetch у браузері)
        else -> AuthError.Network
    }
}
