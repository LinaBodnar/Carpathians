package com.bodnar.carpathians.ui.components

import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.auth_error_invalid_credentials
import carpathians.shared.generated.resources.auth_error_network
import carpathians.shared.generated.resources.auth_error_server
import carpathians.shared.generated.resources.auth_error_session_expired
import carpathians.shared.generated.resources.auth_error_unknown
import com.bodnar.carpathians.domain.auth.AuthError
import org.jetbrains.compose.resources.StringResource

/** Текст снекбара для помилки авторизації. */
val AuthError.message: StringResource
    get() = when (this) {
        AuthError.InvalidCredentials -> Res.string.auth_error_invalid_credentials
        AuthError.Network -> Res.string.auth_error_network
        AuthError.Server -> Res.string.auth_error_server
        AuthError.SessionExpired -> Res.string.auth_error_session_expired
        AuthError.Unknown -> Res.string.auth_error_unknown
    }
