package com.bodnar.carpathians

import com.bodnar.carpathians.domain.auth.AuthError
import com.bodnar.carpathians.domain.auth.AuthRepository
import com.bodnar.carpathians.domain.auth.AuthResult
import com.bodnar.carpathians.domain.auth.AuthTokens
import com.bodnar.carpathians.domain.auth.TokenStorage
import com.bodnar.carpathians.domain.auth.User
import kotlinx.coroutines.flow.MutableSharedFlow

class InMemoryTokenStorage(var tokens: AuthTokens? = null) : TokenStorage {
    override suspend fun getTokens(): AuthTokens? = tokens
    override suspend fun saveTokens(tokens: AuthTokens) {
        this.tokens = tokens
    }

    override suspend fun clear() {
        tokens = null
    }
}

val testUser = User(1, "emilys", "Emily", "Johnson", "emily.johnson@x.dummyjson.com")

class FakeAuthRepository(
    var loginResult: AuthResult<User> = AuthResult.Success(testUser),
    var loggedIn: Boolean = false,
) : AuthRepository {
    var lastLogin: Pair<String, String>? = null
    override val sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override suspend fun login(username: String, password: String): AuthResult<User> {
        lastLogin = username to password
        if (loginResult is AuthResult.Success) loggedIn = true
        return loginResult
    }

    override suspend fun getCurrentUser(): AuthResult<User> =
        if (loggedIn) AuthResult.Success(testUser) else AuthResult.Failure(AuthError.SessionExpired)

    override suspend fun isLoggedIn(): Boolean = loggedIn

    override suspend fun logout() {
        loggedIn = false
    }
}
