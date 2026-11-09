package com.bodnar.carpathians.ui.routes

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.routes_filter_all
import carpathians.shared.generated.resources.routes_subtitle
import carpathians.shared.generated.resources.routes_title
import com.bodnar.carpathians.core.mvi.CollectEffects
import com.bodnar.carpathians.domain.routes.Difficulty
import com.bodnar.carpathians.domain.routes.HikingRoute
import com.bodnar.carpathians.theme.CarpathiansBackgroundBrush
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes
import com.bodnar.carpathians.ui.components.FitText
import com.bodnar.carpathians.ui.components.ScreenTopBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import carpathians.shared.generated.resources.ic_arrow_forward

@Composable
fun RoutesRoute(
    onBack: () -> Unit,
    onOpenRoute: (String) -> Unit,
    viewModel: RoutesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is RoutesEffect.OpenRoute -> onOpenRoute(effect.routeId)
        }
    }
    RoutesScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

/** Екран "Оберіть свій маршрут": фільтр за складністю + картки маршрутів. */
@Composable
fun RoutesScreen(
    state: RoutesState,
    onIntent: (RoutesIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(CarpathiansBackgroundBrush)) {
        ScreenTopBar(title = stringResource(Res.string.routes_title), onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 24.dp + WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = stringResource(Res.string.routes_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CarpathiansColors.TextPleasant,
                    modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth(),
                )
            }
            item {
                DifficultyFilter(
                    selected = state.filter,
                    onSelect = { onIntent(RoutesIntent.FilterSelected(it)) },
                    modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth(),
                )
            }
            if (state.isLoading && state.routes.isEmpty()) {
                item { CircularProgressIndicator(color = CarpathiansColors.Primary) }
            }
            items(state.routes, key = { it.id }) { route ->
                RouteCard(
                    route = route,
                    onClick = { onIntent(RoutesIntent.RouteClicked(route.id)) },
                    modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun DifficultyFilter(
    selected: Difficulty?,
    onSelect: (Difficulty?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf<Difficulty?>(null) + Difficulty.entries
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options) { option ->
            val isSelected = option == selected
            val background by animateColorAsState(if (isSelected) CarpathiansColors.PrimaryDark else CarpathiansColors.Glass)
            Row(
                modifier = Modifier
                    .clip(CarpathiansShapes.Pill)
                    .background(background)
                    .border(1.dp, if (isSelected) CarpathiansColors.Primary else CarpathiansColors.GlassBorder, CarpathiansShapes.Pill)
                    .clickable(role = Role.Tab) { onSelect(option) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (option != null) {
                    Box(Modifier.size(8.dp).clip(CarpathiansShapes.Pill).background(option.color))
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = stringResource(option?.label ?: Res.string.routes_filter_all),
                    style = MaterialTheme.typography.titleSmall,
                    color = CarpathiansColors.White,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun RouteCard(
    route: HikingRoute,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(CarpathiansShapes.Card)
            .background(CarpathiansColors.Glass)
            .border(1.dp, CarpathiansColors.GlassBorder, CarpathiansShapes.Card)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Image(
            painter = painterResource(route.cover),
            contentDescription = stringResource(route.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
        )
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FitText(
                    text = stringResource(route.name).uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 1.5.sp),
                    color = CarpathiansColors.White,
                    minFontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_forward),
                    contentDescription = null,
                    tint = CarpathiansColors.Primary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
            RouteStats(route)
        }
    }
}
