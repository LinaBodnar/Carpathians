package com.bodnar.carpathians.data.auth.remote

import com.bodnar.carpathians.data.auth.SessionEvents
import com.bodnar.carpathians.domain.auth.TokenStorage
import com.bodnar.carpathians.logging.AppLogger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

private val log = AppLogger.withTag("Http")

val AppJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

/**
 * HTTP-клієнт застосунку:
 *  - JSON (kotlinx.serialization), базова адреса DummyJSON, тайм-аути;
 *  - Bearer-авторизація: access-токен береться з [TokenStorage] і додається до запитів;
 *  - автоматичне оновлення токенів: на відповідь 401 клієнт викликає /auth/refresh,
 *    зберігає нову пару токенів і повторює початковий запит;
 *  - якщо оновити не вдалося, сесія очищається і надсилається подія [SessionEvents.expired].
 *
 * @param engine рушій (null — платформний за замовчуванням: OkHttp або fetch у браузері)
 */
fun createHttpClient(
    tokenStorage: TokenStorage,
    sessionEvents: SessionEvents,
    engine: HttpClientEngine? = null,
): HttpClient {
    val config: HttpClientConfig<*>.() -> Unit = {
        expectSuccess = true

        install(ContentNegotiation) { json(AppJson) }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
        }

        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) = log.d { message }
            }
            sanitizeHeader { it == HttpHeaders.Authorization }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    tokenStorage.getTokens()?.let { BearerTokens(it.accessToken, it.refreshToken) }
                }

                refreshTokens {
                    val refreshToken = oldTokens?.refreshToken ?: tokenStorage.getTokens()?.refreshToken
                    if (refreshToken == null) {
                        log.w { "No refresh token, session expired" }
                        sessionEvents.notifyExpired()
                        return@refreshTokens null
                    }
                    try {
                        log.i { "Access token expired, refreshing..." }
                        val tokens = client.post(AuthApi.REFRESH_PATH) {
                            markAsRefreshTokenRequest()
                            setBody(RefreshRequestDto(refreshToken, AuthApi.ACCESS_TOKEN_LIFETIME_MINUTES))
                        }.body<TokensDto>().toTokens()
                        tokenStorage.saveTokens(tokens)
                        log.i { "Tokens refreshed" }
                        BearerTokens(tokens.accessToken, tokens.refreshToken)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        log.w(e) { "Token refresh failed, session expired" }
                        tokenStorage.clear()
                        sessionEvents.notifyExpired()
                        null
                    }
                }

                // Токен не надсилається у запитах входу, реєстрації та оновлення
                sendWithoutRequest { request ->
                    val path = request.url.pathSegments.joinToString("/")
                    listOf(AuthApi.LOGIN_PATH, AuthApi.REFRESH_PATH, AuthApi.REGISTER_PATH)
                        .none { path.endsWith(it) }
                }
            }
        }

        defaultRequest {
            url(AuthApi.BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }
    return if (engine != null) HttpClient(engine, config) else HttpClient(config)
}

/** Скидає закешовані в Ktor токени (після входу або виходу). */
fun HttpClient.clearCachedTokens() {
    authProvider<BearerAuthProvider>()?.clearToken()
}
