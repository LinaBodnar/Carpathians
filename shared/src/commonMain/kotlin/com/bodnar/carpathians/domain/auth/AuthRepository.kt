package com.bodnar.carpathians.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Вхід за іменем і паролем; токени зберігаються в безпечному сховищі. */
    suspend fun login(username: String, password: String): AuthResult<User>

    /** Профіль поточного користувача (захищений запит з access-токеном). */
    suspend fun getCurrentUser(): AuthResult<User>

    /** Чи є збережена сесія (токени) на пристрої. */
    suspend fun isLoggedIn(): Boolean

    /** Видаляє токени з пристрою. */
    suspend fun logout()

    /** Подія: сесія закінчилась (refresh-токен недійсний), потрібно увійти знову. */
    val sessionExpired: Flow<Unit>
}

/** Безпечне сховище токенів. */
interface TokenStorage {
    suspend fun getTokens(): AuthTokens?
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun clear()
}
