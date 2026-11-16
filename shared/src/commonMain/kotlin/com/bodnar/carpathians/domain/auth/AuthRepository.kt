package com.bodnar.carpathians.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): AuthResult<User>

    suspend fun register(data: SignUpData): AuthResult<User>

    suspend fun getCurrentUser(): AuthResult<User>

    suspend fun isLoggedIn(): Boolean

    suspend fun logout()

    val sessionExpired: Flow<Unit>
}

interface TokenStorage {
    suspend fun getTokens(): AuthTokens?
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun clear()
}
