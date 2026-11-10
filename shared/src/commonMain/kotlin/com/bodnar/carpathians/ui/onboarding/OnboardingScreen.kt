package com.bodnar.carpathians.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bodnar.carpathians.core.mvi.CollectEffects
import com.bodnar.carpathians.ui.components.AppLanguageSwitcher
import com.bodnar.carpathians.ui.components.FitText
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.ui.components.AdventureTitle
import com.bodnar.carpathians.ui.components.BrandLogo
import com.bodnar.carpathians.ui.components.GlassChip
import com.bodnar.carpathians.ui.components.GradientButton
import com.bodnar.carpathians.ui.components.PagerIndicator
import com.bodnar.carpathians.ui.components.RouteButton
import com.bodnar.carpathians.ui.components.TextShadow
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.onboarding_next
import carpathians.shared.generated.resources.onboarding_skip
import carpathians.shared.generated.resources.onboarding_start
import carpathians.shared.generated.resources.onboarding_step
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue

/**
 * Точка входу екрана для навігації: бере ViewModel з Koin,
 * передає стан у UI та обробляє одноразові події.
 */
@Composable
fun OnboardingRoute(
    onNavigateToAuth: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.pageCount },
    )

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is OnboardingEffect.ScrollToPage -> pagerState.animateScrollToPage(effect.page)
            OnboardingEffect.NavigateToAuth -> onNavigateToAuth()
        }
    }

    OnboardingScreen(
        state = state,
        pagerState = pagerState,
        onIntent = viewModel::onIntent,
        topBarActions = { AppLanguageSwitcher(Modifier.padding(end = 8.dp)) },
    )
}

/**
 * Екран знайомства з додатком на основі HorizontalPager.
 * Не містить логіки: лише відображає [state] і надсилає [OnboardingIntent].
 */
@Composable
fun OnboardingScreen(
    state: OnboardingState,
    pagerState: PagerState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
    topBarActions: @Composable RowScope.() -> Unit = {},
) {
    val pages = onboardingPages
    val isLastPage = state.isLastPage

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onIntent(OnboardingIntent.PageChanged(page))
        }
    }

    Box(modifier = modifier.fillMaxSize().background(CarpathiansColors.BackgroundBottom)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { it },
        ) { index ->
            OnboardingPageContent(
                page = pages[index],
                pageIndex = index,
                pagerState = pagerState,
            )
        }

        // Верхня панель: логотип, дії, "Пропустити"
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrandLogo(Modifier.weight(1f).padding(end = 8.dp))
            topBarActions()
            AnimatedVisibility(visible = !isLastPage, enter = fadeIn(), exit = fadeOut()) {
                TextButton(onClick = { onIntent(OnboardingIntent.SkipClicked) }) {
                    Text(
                        text = stringResource(Res.string.onboarding_skip),
                        style = MaterialTheme.typography.titleSmall,
                        color = CarpathiansColors.TextNav,
                        maxLines = 1,
                    )
                }
            }
        }

        // Нижня панель: індикатор + кнопка
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PagerIndicator(pageCount = state.pageCount, currentPage = state.currentPage)
            Spacer(Modifier.weight(1f))
            AnimatedContent(
                targetState = isLastPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
            ) { last ->
                if (last) {
                    GradientButton(
                        text = stringResource(Res.string.onboarding_start),
                        onClick = { onIntent(OnboardingIntent.FinishClicked) },
                        large = true,
                    )
                } else {
                    RouteButton(
                        text = stringResource(Res.string.onboarding_next),
                        showArrow = true,
                        onClick = { onIntent(OnboardingIntent.NextClicked) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    pagerState: PagerState,
) {
    // Відстань сторінки від центру: 0 — поточна, ±1 — сусідні
    val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction

    Box(Modifier.fillMaxSize()) {
        // Фото з ефектом паралаксу
        Image(
            painter = painterResource(page.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = pageOffset * size.width * 0.5f
                    val scale = 1.1f - 0.1f * (1f - pageOffset.absoluteValue.coerceIn(0f, 1f))
                    scaleX = scale
                    scaleY = scale
                },
        )
        // Затемнення, щоб текст читався (як hero + gradient фону)
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.55f),
                        0.3f to Color.Transparent,
                        0.5f to CarpathiansColors.BackgroundTop.copy(alpha = 0.35f),
                        1f to CarpathiansColors.BackgroundBottom,
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                .padding(start = 24.dp, end = 24.dp, bottom = 110.dp)
                .graphicsLayer {
                    // Текст плавно зникає при перегортанні
                    alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                    translationX = pageOffset * size.width * -0.2f
                },
        ) {
            Text(
                text = stringResource(Res.string.onboarding_step, pageIndex + 1, pagerState.pageCount),
                style = MaterialTheme.typography.labelMedium,
                color = CarpathiansColors.Primary,
            )
            Spacer(Modifier.height(12.dp))
            if (page.showAdventureTitle) {
                AdventureTitle(fontSize = 54)
                Spacer(Modifier.height(8.dp))
            }
            FitText(
                text = stringResource(page.title),
                style = MaterialTheme.typography.displaySmall.copy(shadow = TextShadow),
                color = CarpathiansColors.White,
                minFontSize = 18.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(page.description),
                style = MaterialTheme.typography.bodyLarge.copy(shadow = TextShadow),
                color = CarpathiansColors.TextPleasant,
            )
            if (page.chips.isNotEmpty()) {
                Spacer(Modifier.height(18.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    page.chips.forEach { chip ->
                        GlassChip(text = stringResource(chip.text), dotColor = chip.dotColor)
                    }
                }
            }
        }
    }
}
