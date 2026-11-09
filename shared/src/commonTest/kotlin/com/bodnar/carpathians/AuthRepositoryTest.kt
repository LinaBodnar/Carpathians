package com.bodnar.carpathians

import com.bodnar.carpathians.data.auth.AuthRepositoryImpl
import com.bodnar.carpathians.data.auth.SessionEvents
import com.bodnar.carpathians.data.auth.remote.AuthApi
import com.bodnar.carpathians.data.auth.remote.createHttpClient
import com.bodnar.carpathians.domain.auth.AuthError
import com.bodnar.carpathians.domain.auth.AuthResult
import com.bodnar.carpathians.domain.auth.AuthTokens
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val storage = InMemoryTokenStorage()
    private val sessionEvents = SessionEvents()
    private val requests = mutableListOf<HttpRequestData>()

    private fun repository(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): AuthRepositoryImpl {
        val engine = MockEngine { request ->
            requests += request
            handler(request)
        }
        val client = createHttpClient(storage, sessionEvents, engine)
        return AuthRepositoryImpl(AuthApi(client), client, storage, sessionEvents)
    }

    private fun MockRequestHandleScope.json(body: String, status: HttpStatusCode = HttpStatusCode.OK) =
        respond(body, status, jsonHeaders)

    private val HttpRequestData.path get() = url.encodedPath
    private val HttpRequestData.bodyText get() = (body as OutgoingContent.ByteArrayContent).bytes().decodeToString()

    @Test
    fun loginSavesTokensAndReturnsUser() = runTest {
        val repo = repository {
            json("""{"id":1,"username":"emilys","email":"e@x.com","firstName":"Emily","lastName":"Johnson",
                "gender":"female","image":"img","accessToken":"access-1","refreshToken":"refresh-1"}""")
        }

        val result = repo.login("emilys", "emilyspass")

        assertEquals("Emily", (result as AuthResult.Success).data.firstName)
        assertEquals(AuthTokens("access-1", "refresh-1"), storage.tokens)
        assertTrue(repo.isLoggedIn())
        assertEquals("/auth/login", requests.single().path)
        assertTrue(requests.single().bodyText.contains("\"username\":\"emilys\""))
        assertNull(requests.single().headers[HttpHeaders.Authorization])
    }

    @Test
    fun wrongPasswordIsInvalidCredentials() = runTest {
        val repo = repository { json("""{"message":"Invalid credentials"}""", HttpStatusCode.BadRequest) }

        assertEquals(AuthResult.Failure(AuthError.InvalidCredentials), repo.login("emilys", "wrong"))
        assertNull(storage.tokens)
    }

    @Test
    fun serverErrorIsMapped() = runTest {
        val repo = repository { respondError(HttpStatusCode.ServiceUnavailable) }
        assertEquals(AuthResult.Failure(AuthError.Server), repo.login("emilys", "emilyspass"))
    }

    @Test
    fun connectionProblemIsNetworkError() = runTest {
        val repo = repository { throw RuntimeException("connection refused") }
        assertEquals(AuthResult.Failure(AuthError.Network), repo.login("emilys", "emilyspass"))
    }

    @Test
    fun expiredAccessTokenIsRefreshedAutomatically() = runTest {
        storage.tokens = AuthTokens("expired", "refresh-1")
        val repo = repository { request ->
            when (request.path) {
                "/auth/me" ->
                    if (request.headers[HttpHeaders.Authorization] == "Bearer access-2") {
                        json("""{"id":1,"username":"emilys","firstName":"Emily","lastName":"Johnson"}""")
                    } else {
                        json("""{"message":"Token Expired!"}""", HttpStatusCode.Unauthorized)
                    }

                "/auth/refresh" -> json("""{"accessToken":"access-2","refreshToken":"refresh-2"}""")
                else -> respondError(HttpStatusCode.NotFound)
            }
        }

        val result = repo.getCurrentUser()

        assertEquals("emilys", (result as AuthResult.Success).data.username)
        assertEquals(AuthTokens("access-2", "refresh-2"), storage.tokens)
        assertEquals(listOf("/auth/me", "/auth/refresh", "/auth/me"), requests.map { it.path })
        assertTrue(requests[1].bodyText.contains("\"refreshToken\":\"refresh-1\""))
    }

    @Test
    fun failedRefreshEndsSession() = runTest {
        storage.tokens = AuthTokens("expired", "bad-refresh")
        val repo = repository { request ->
            when (request.path) {
                "/auth/refresh" -> json("""{"message":"Invalid refresh token"}""", HttpStatusCode.Forbidden)
                else -> json("""{"message":"Token Expired!"}""", HttpStatusCode.Unauthorized)
            }
        }
        val expired = async { sessionEvents.expired.first() }
        yield()

        assertEquals(AuthResult.Failure(AuthError.SessionExpired), repo.getCurrentUser())
        assertNull(storage.tokens)
        expired.await()
    }

    @Test
    fun logoutClearsTokens() = runTest {
        storage.tokens = AuthTokens("a", "r")
        repository { respondError(HttpStatusCode.NotFound) }.logout()
        assertNull(storage.tokens)
    }
}
