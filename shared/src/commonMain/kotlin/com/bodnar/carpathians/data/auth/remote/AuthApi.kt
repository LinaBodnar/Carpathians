package com.bodnar.carpathians.data.auth.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/** Запити авторизації DummyJSON (https://dummyjson.com/docs/auth). */
class AuthApi(private val client: HttpClient) {

    suspend fun login(username: String, password: String): LoginResponseDto =
        client.post(LOGIN_PATH) {
            setBody(LoginRequestDto(username, password, ACCESS_TOKEN_LIFETIME_MINUTES))
        }.body()

    suspend fun register(request: RegisterRequestDto): UserDto =
        client.post(REGISTER_PATH) { setBody(request) }.body()

    suspend fun me(): UserDto = client.get(ME_PATH).body()

    companion object {
        const val BASE_URL = "https://dummyjson.com/"
        const val LOGIN_PATH = "auth/login"
        const val REFRESH_PATH = "auth/refresh"
        const val ME_PATH = "auth/me"
        const val REGISTER_PATH = "users/add"

        /**
         * Короткий час життя access-токена (1 хв), щоб автоматичне оновлення
         * через refresh-токен було видно під час роботи з застосунком.
         */
        const val ACCESS_TOKEN_LIFETIME_MINUTES = 1
    }
}
