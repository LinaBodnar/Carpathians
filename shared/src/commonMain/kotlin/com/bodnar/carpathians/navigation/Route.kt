package com.bodnar.carpathians.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/** Екрани застосунку (ключі Navigation 3). */
@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Auth : Route
}

/**
 * Конфігурація збереження back stack. На не-Android платформах Navigation 3
 * потребує явної реєстрації всіх реалізацій [NavKey] для серіалізації.
 */
val navSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Route.Onboarding::class, Route.Onboarding.serializer())
            subclass(Route.Auth::class, Route.Auth.serializer())
        }
    }
}
