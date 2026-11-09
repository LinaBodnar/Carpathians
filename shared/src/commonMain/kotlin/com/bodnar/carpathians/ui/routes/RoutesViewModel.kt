package com.bodnar.carpathians.ui.routes

import androidx.lifecycle.viewModelScope
import com.bodnar.carpathians.core.mvi.MviViewModel
import com.bodnar.carpathians.domain.auth.validation.ValidateNameUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePeopleCountUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePhoneUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidationError
import com.bodnar.carpathians.domain.routes.Difficulty
import com.bodnar.carpathians.domain.routes.GetRouteDetailsUseCase
import com.bodnar.carpathians.domain.routes.GetRoutesUseCase
import com.bodnar.carpathians.logging.AppLogger
import kotlinx.coroutines.launch

class RoutesViewModel(
    private val getRoutes: GetRoutesUseCase,
) : MviViewModel<RoutesState, RoutesIntent, RoutesEffect>(RoutesState()) {

    init {
        load(filter = null)
    }

    override fun onIntent(intent: RoutesIntent) {
        when (intent) {
            is RoutesIntent.FilterSelected -> if (intent.difficulty != currentState.filter) load(intent.difficulty)
            is RoutesIntent.RouteClicked -> sendEffect(RoutesEffect.OpenRoute(intent.routeId))
        }
    }

    private fun load(filter: Difficulty?) {
        setState { copy(filter = filter, isLoading = true) }
        viewModelScope.launch {
            val routes = getRoutes(filter)
            setState { copy(routes = routes, isLoading = false) }
        }
    }
}

class RouteDetailsViewModel(
    private val routeId: String,
    private val getRouteDetails: GetRouteDetailsUseCase,
    private val validateName: ValidateNameUseCase,
    private val validatePhone: ValidatePhoneUseCase,
    private val validatePeople: ValidatePeopleCountUseCase,
) : MviViewModel<RouteDetailsState, RouteDetailsIntent, RouteDetailsEffect>(RouteDetailsState()) {

    private val log = AppLogger.withTag("RouteDetailsVM")

    init {
        viewModelScope.launch {
            val route = getRouteDetails(routeId)
            setState { copy(route = route, isLoading = false, isNotFound = route == null) }
        }
    }

    override fun onIntent(intent: RouteDetailsIntent) {
        when (intent) {
            RouteDetailsIntent.OpenMap -> currentState.route?.let { sendEffect(RouteDetailsEffect.OpenUrl(it.mapUrl)) }
            RouteDetailsIntent.BookClicked -> setState { copy(booking = BookingForm()) }
            RouteDetailsIntent.DismissBooking -> setState { copy(booking = null) }
            is RouteDetailsIntent.BookingFieldChanged -> updateBooking { form ->
                val values = form.values + (intent.field to intent.value)
                form.copy(values = values, errors = if (form.showValidation) validate(values) else emptyMap())
            }
            RouteDetailsIntent.SubmitBooking -> submitBooking()
        }
    }

    private fun updateBooking(transform: (BookingForm) -> BookingForm) {
        setState { copy(booking = booking?.let(transform)) }
    }

    private fun validate(values: Map<BookingField, String>): Map<BookingField, ValidationError> = buildMap {
        validateName(values[BookingField.Name].orEmpty())?.let { put(BookingField.Name, it) }
        validatePhone(values[BookingField.Phone].orEmpty())?.let { put(BookingField.Phone, it) }
        validatePeople(values[BookingField.People].orEmpty())?.let { put(BookingField.People, it) }
    }

    private fun submitBooking() {
        val form = currentState.booking ?: return
        val errors = validate(form.values)
        updateBooking { it.copy(errors = errors, showValidation = true) }

        val firstInvalid = BookingField.entries.firstOrNull { it in errors }
        if (firstInvalid != null) {
            sendEffect(RouteDetailsEffect.FocusBookingField(firstInvalid))
            return
        }
        log.i { "Booking accepted: route=$routeId, people=${form.value(BookingField.People)}" }
        setState { copy(booking = null) }
        sendEffect(RouteDetailsEffect.BookingAccepted)
    }
}
