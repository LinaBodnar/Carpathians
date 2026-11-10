package com.bodnar.carpathians.ui.routes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import carpathians.shared.generated.resources.*
import com.bodnar.carpathians.core.mvi.CollectEffects
import com.bodnar.carpathians.domain.auth.validation.ValidationError
import com.bodnar.carpathians.domain.routes.HikingRoute
import com.bodnar.carpathians.domain.routes.RoutePoint
import com.bodnar.carpathians.logging.AppLogger
import com.bodnar.carpathians.theme.CarpathiansBackgroundBrush
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes
import com.bodnar.carpathians.ui.components.AppSnackbarHost
import com.bodnar.carpathians.ui.components.FitText
import com.bodnar.carpathians.ui.components.GradientButton
import com.bodnar.carpathians.ui.components.RouteButton
import com.bodnar.carpathians.ui.components.ScreenTopBar
import com.bodnar.carpathians.ui.components.localized
import com.bodnar.carpathians.ui.components.showMessage
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private val log = AppLogger.withTag("RouteDetails")

@Composable
fun RouteDetailsRoute(
    routeId: String,
    onBack: () -> Unit,
    viewModel: RouteDetailsViewModel = koinViewModel(key = "route-$routeId") { parametersOf(routeId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    val bookingFocus = remember { BookingField.entries.associateWith { FocusRequester() } }

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is RouteDetailsEffect.OpenUrl -> runCatching { uriHandler.openUri(effect.url) }
                .onFailure { log.w(it) { "Cannot open ${effect.url}" } }
            is RouteDetailsEffect.FocusBookingField -> bookingFocus.getValue(effect.field).requestFocus()
            RouteDetailsEffect.BookingAccepted -> snackbarHostState.showMessage(getString(Res.string.booking_success))
        }
    }

    RouteDetailsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
        bookingFocus = bookingFocus,
    )
}

@Composable
fun RouteDetailsScreen(
    state: RouteDetailsState,
    onIntent: (RouteDetailsIntent) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    bookingFocus: Map<BookingField, FocusRequester>,
    modifier: Modifier = Modifier,
) {
    val route = state.route
    Box(modifier.fillMaxSize().background(CarpathiansBackgroundBrush)) {
        Column(Modifier.fillMaxSize()) {
            ScreenTopBar(
                title = route?.let { stringResource(it.name) } ?: stringResource(Res.string.routes_title),
                onBack = onBack,
            )
            when {
                route != null -> RouteContent(route, onIntent)
                state.isNotFound -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.route_not_found), color = CarpathiansColors.TextPleasant)
                }
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CarpathiansColors.Primary)
                }
            }
        }

        AppSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
        )
    }

    val booking = state.booking
    if (booking != null && route != null) {
        BookingDialog(
            routeName = stringResource(route.name),
            form = booking,
            focus = bookingFocus,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun RouteContent(route: HikingRoute, onIntent: (RouteDetailsIntent) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 96.dp + WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding(),
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            RouteCover(
                route,
                Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .clip(CarpathiansShapes.Card),
            )
        }
        item {
            Column(Modifier.widthIn(max = 640.dp).fillMaxWidth().padding(16.dp)) {
                RouteStats(route)
                Spacer(Modifier.height(16.dp))
                GradientButton(
                    text = stringResource(Res.string.route_book),
                    onClick = { onIntent(RouteDetailsIntent.BookClicked) },
                    large = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                RouteButton(
                    text = stringResource(Res.string.route_open_map),
                    onClick = { onIntent(RouteDetailsIntent.OpenMap) },
                    showArrow = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(24.dp))
                FitText(
                    text = stringResource(Res.string.route_points_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = CarpathiansColors.White,
                    minFontSize = 16.sp,
                )
            }
        }
        itemsIndexed(route.points) { index, point ->
            RoutePointItem(
                point = point,
                isFirst = index == 0,
                isLast = index == route.points.lastIndex,
                modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth().padding(horizontal = 16.dp),
            )
        }
    }
}

/** Точка маршруту у вигляді таймлайну (як акордеон у дизайні). */
@Composable
private fun RoutePointItem(
    point: RoutePoint,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    // Лінія таймлайну малюється на всю висоту рядка (від точки — для першої, до точки — для останньої)
    val lineColor = CarpathiansColors.PrimaryDark
    Row(
        modifier.drawBehind {
            val x = 12.dp.toPx()
            val dotCenter = 19.dp.toPx()
            drawLine(
                color = lineColor,
                start = Offset(x, if (isFirst) dotCenter else 0f),
                end = Offset(x, if (isLast) dotCenter else size.height),
                strokeWidth = 2.dp.toPx(),
            )
        }
    ) {
        Box(Modifier.width(24.dp), contentAlignment = Alignment.TopCenter) {
            Box(
                Modifier
                    .padding(top = 12.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (isFirst || isLast) CarpathiansColors.Primary else CarpathiansColors.BackgroundTop)
                    .border(2.dp, CarpathiansColors.Primary, CircleShape)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(bottom = 20.dp)
                .clip(CarpathiansShapes.Card)
                .background(CarpathiansColors.Glass)
                .border(1.dp, CarpathiansColors.GlassBorder, CarpathiansShapes.Card),
        ) {
            Image(
                painter = painterResource(point.image),
                contentDescription = point.title.localized(),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 10f),
            )
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FitText(
                        text = point.title.localized(),
                        style = MaterialTheme.typography.titleMedium,
                        color = CarpathiansColors.White,
                        minFontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                    )
                    point.altitude?.let {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.route_altitude_m, it),
                            style = MaterialTheme.typography.labelMedium,
                            color = CarpathiansColors.Primary,
                            maxLines = 1,
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = point.description.localized(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CarpathiansColors.TextPleasant,
                )
            }
        }
    }
}

/** Вікно "Запис у похід" (секція .booking з дизайну). */
@Composable
private fun BookingDialog(
    routeName: String,
    form: BookingForm,
    focus: Map<BookingField, FocusRequester>,
    onIntent: (RouteDetailsIntent) -> Unit,
) {
    Dialog(onDismissRequest = { onIntent(RouteDetailsIntent.DismissBooking) }) {
        Column(
            Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .clip(CarpathiansShapes.Modal)
                .background(CarpathiansColors.MenuGlass)
                .border(1.dp, CarpathiansColors.GlassBorderStrong, CarpathiansShapes.Modal)
                .padding(20.dp),
        ) {
            FitText(
                text = stringResource(Res.string.booking_title),
                style = MaterialTheme.typography.headlineSmall,
                color = CarpathiansColors.White,
                minFontSize = 16.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.booking_subtitle, routeName),
                style = MaterialTheme.typography.bodyMedium,
                color = CarpathiansColors.TextPleasant,
            )
            Spacer(Modifier.height(16.dp))

            val fields = BookingField.entries
            fields.forEachIndexed { index, field ->
                val isLast = index == fields.lastIndex
                BookingTextField(
                    field = field,
                    value = form.value(field),
                    error = form.errors[field],
                    onValueChange = { onIntent(RouteDetailsIntent.BookingFieldChanged(field, it)) },
                    imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
                    keyboardActions = KeyboardActions(
                        onNext = { focus.getValue(fields[index + 1]).requestFocus() },
                        onDone = { onIntent(RouteDetailsIntent.SubmitBooking) },
                    ),
                    modifier = Modifier.focusRequester(focus.getValue(field)),
                )
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(8.dp))
            GradientButton(
                text = stringResource(Res.string.booking_submit),
                onClick = { onIntent(RouteDetailsIntent.SubmitBooking) },
                large = true,
                modifier = Modifier.fillMaxWidth(),
            )
            TextButton(
                onClick = { onIntent(RouteDetailsIntent.DismissBooking) },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(stringResource(Res.string.booking_cancel), color = CarpathiansColors.TextNav)
            }
        }
    }
}

@Composable
private fun BookingTextField(
    field: BookingField,
    value: String,
    error: ValidationError?,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
) {
    val label = when (field) {
        BookingField.Name -> Res.string.booking_name
        BookingField.Phone -> Res.string.booking_phone
        BookingField.People -> Res.string.booking_people
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label), maxLines = 1) },
        isError = error != null,
        supportingText = error?.let { { Text(bookingErrorMessage(it)) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = when (field) {
                BookingField.Name -> KeyboardType.Text
                BookingField.Phone -> KeyboardType.Phone
                BookingField.People -> KeyboardType.Number
            },
            capitalization = if (field == BookingField.Name) KeyboardCapitalization.Words else KeyboardCapitalization.None,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
        shape = CarpathiansShapes.Chip,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = CarpathiansColors.White,
            unfocusedTextColor = CarpathiansColors.White,
            focusedContainerColor = CarpathiansColors.Glass,
            unfocusedContainerColor = CarpathiansColors.Glass,
            errorContainerColor = CarpathiansColors.Glass,
            focusedBorderColor = CarpathiansColors.Primary,
            unfocusedBorderColor = CarpathiansColors.GlassBorderStrong,
            focusedLabelColor = CarpathiansColors.Primary,
            unfocusedLabelColor = CarpathiansColors.TextNav,
            cursorColor = CarpathiansColors.Primary,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun bookingErrorMessage(error: ValidationError): String = when (error) {
    ValidationError.Empty -> stringResource(Res.string.auth_error_empty)
    is ValidationError.TooShort -> stringResource(Res.string.auth_error_too_short, error.minLength)
    ValidationError.InvalidName -> stringResource(Res.string.auth_error_invalid_name)
    ValidationError.InvalidPhone -> stringResource(Res.string.booking_error_phone)
    is ValidationError.OutOfRange -> stringResource(Res.string.booking_error_people, error.min, error.max)
    else -> stringResource(Res.string.auth_error_unknown)
}
