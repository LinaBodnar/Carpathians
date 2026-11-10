package com.bodnar.carpathians.ui.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.difficulty_easy
import carpathians.shared.generated.resources.difficulty_hard
import carpathians.shared.generated.resources.difficulty_medium
import carpathians.shared.generated.resources.route_altitude_m
import carpathians.shared.generated.resources.route_duration_one_day
import carpathians.shared.generated.resources.route_duration_two_days
import carpathians.shared.generated.resources.route_length_km
import com.bodnar.carpathians.domain.routes.Difficulty
import com.bodnar.carpathians.domain.routes.HikingRoute
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.ui.components.GlassChip
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

val Difficulty.label: StringResource
    get() = when (this) {
        Difficulty.Easy -> Res.string.difficulty_easy
        Difficulty.Medium -> Res.string.difficulty_medium
        Difficulty.Hard -> Res.string.difficulty_hard
    }

val Difficulty.color: Color
    get() = when (this) {
        Difficulty.Easy -> CarpathiansColors.Primary
        Difficulty.Medium -> CarpathiansColors.Warning
        Difficulty.Hard -> CarpathiansColors.Danger
    }

/** Складність, довжина, тривалість і найвища точка маршруту у вигляді чіпів. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RouteStats(route: HikingRoute, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GlassChip(text = stringResource(route.difficulty.label), dotColor = route.difficulty.color)
        GlassChip(text = stringResource(Res.string.route_length_km, route.lengthKm))
        GlassChip(
            text = stringResource(
                if (route.maxDays > 1) Res.string.route_duration_two_days else Res.string.route_duration_one_day
            )
        )
        GlassChip(text = stringResource(Res.string.route_altitude_m, route.maxAltitude), dotColor = CarpathiansColors.Info)
    }
}

/**
 * Обкладинка маршруту 16:9. Фото трохи збільшене й обрізане,
 * щоб сховати нерівні краї вихідних зображень з дизайну.
 */
@Composable
fun RouteCover(route: HikingRoute, modifier: Modifier = Modifier) {
    Box(modifier.aspectRatio(16f / 9f).clipToBounds()) {
        Image(
            painter = painterResource(route.cover),
            contentDescription = stringResource(route.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().scale(1.04f),
        )
    }
}
