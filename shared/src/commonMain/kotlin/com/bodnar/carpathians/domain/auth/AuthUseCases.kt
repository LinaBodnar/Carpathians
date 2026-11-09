package com.bodnar.carpathians.domain.auth

import kotlinx.coroutines.flow.Flow

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): AuthResult<User> =
        repository.login(username.trim(), password)
}

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(data: SignUpData): AuthResult<User> =
        repository.register(
            data.copy(
                firstName = data.firstName.trim(),
                username = data.username.trim(),
                email = data.email.trim(),
            )
        )
}

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): AuthResult<User> = repository.getCurrentUser()
}

class IsUserLoggedInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Boolean = repository.isLoggedIn()
}

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}

class ObserveSessionExpiredUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<Unit> = repository.sessionExpired
}
