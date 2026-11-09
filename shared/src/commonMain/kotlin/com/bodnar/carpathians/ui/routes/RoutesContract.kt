package com.bodnar.carpathians.ui.routes

import com.bodnar.carpathians.domain.auth.validation.ValidationError
import com.bodnar.carpathians.domain.routes.Difficulty
import com.bodnar.carpathians.domain.routes.HikingRoute

// ---------- Список маршрутів ----------

data class RoutesState(
    val routes: List<HikingRoute> = emptyList(),
    /** null — усі маршрути. */
    val filter: Difficulty? = null,
    val isLoading: Boolean = true,
)

sealed interface RoutesIntent {
    data class FilterSelected(val difficulty: Difficulty?) : RoutesIntent
    data class RouteClicked(val routeId: String) : RoutesIntent
}

sealed interface RoutesEffect {
    data class OpenRoute(val routeId: String) : RoutesEffect
}

// ---------- Деталі маршруту + запис у похід ----------

enum class BookingField { Name, Phone, People }

data class BookingForm(
    val values: Map<BookingField, String> = mapOf(BookingField.People to "1"),
    val errors: Map<BookingField, ValidationError> = emptyMap(),
    val showValidation: Boolean = false,
) {
    fun value(field: BookingField): String = values[field].orEmpty()
}

data class RouteDetailsState(
    val route: HikingRoute? = null,
    val isLoading: Boolean = true,
    val isNotFound: Boolean = false,
    /** Відкрите вікно "Запис у похід" (null — закрите). */
    val booking: BookingForm? = null,
)

sealed interface RouteDetailsIntent {
    data object OpenMap : RouteDetailsIntent
    data object BookClicked : RouteDetailsIntent
    data class BookingFieldChanged(val field: BookingField, val value: String) : RouteDetailsIntent
    data object SubmitBooking : RouteDetailsIntent
    data object DismissBooking : RouteDetailsIntent
}

sealed interface RouteDetailsEffect {
    data class OpenUrl(val url: String) : RouteDetailsEffect
    data class FocusBookingField(val field: BookingField) : RouteDetailsEffect
    data object BookingAccepted : RouteDetailsEffect
}
