package com.bodnar.carpathians.ui.home

import com.bodnar.carpathians.domain.auth.AuthError
import com.bodnar.carpathians.domain.auth.User

data class HomeState(
    val user: User? = null,
    val isLoading: Boolean = false,
)

sealed interface HomeIntent {
    data object Refresh : HomeIntent
    data object Logout : HomeIntent
}

sealed interface HomeEffect {
    data class ShowError(val error: AuthError) : HomeEffect
    data object NavigateToAuth : HomeEffect
}
