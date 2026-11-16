package com.bodnar.carpathians.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.onboarding_next
import carpathians.shared.generated.resources.onboarding_skip
import carpathians.shared.generated.resources.onboarding_start
import carpathians.shared.generated.resources.onboarding_step
import com.bodnar.carpathians.core.mvi.CollectEffects
import com.bodnar.carpathians.theme.CarpathiansBackgroundBrush
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.ui.adaptive.ScreenLayout
import com.bodnar.carpathians.ui.adaptive.currentScreenLayout
import com.bodnar.carpathians.ui.components.AdventureTitle
import com.bodnar.carpathians.ui.components.AppLanguageSwitcher
import com.bodnar.carpathians.ui.components.BrandLogo
import com.bodnar.carpathians.ui.components.FitText
import com.bodnar.carpathians.ui.components.GlassChip
import com.bodnar.carpathians.ui.components.GradientButton
import com.bodnar.carpathians.ui.components.PagerIndicator
import com.bodnar.carpathians.ui.components.RouteButton
import com.bodnar.carpathians.ui.components.TextShadow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.absoluteValue

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
        layout = currentScreenLayout(),
        topBarActions = { AppLanguageSwitcher(Modifier.padding(end = 8.dp)) },
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    pagerState: PagerState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
    layout: ScreenLayout = ScreenLayout.Compact,
    topBarActions: @Composable RowScope.() -> Unit = {},
) {
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onIntent(OnboardingIntent.PageChanged(page))
        }
    }

    when (layout) {
        ScreenLayout.Expanded -> OnboardingTwoPane(state, pagerState, onIntent, modifier, topBarActions)
        ScreenLayout.Medium -> OnboardingStacked(
            state = state,
            pagerState = pagerState,
            onIntent = onIntent,
            modifier = modifier,
            horizontalPadding = 48.dp,
            contentMaxWidth = 720.dp,
            textStyle = OnboardingTextStyle.Large,
            topBarActions = topBarActions,
        )
        ScreenLayout.Compact -> OnboardingStacked(
            state = state,
            pagerState = pagerState,
            onIntent = onIntent,
            modifier = modifier,
            horizontalPadding = 24.dp,
            contentMaxWidth = 640.dp,
            textStyle = OnboardingTextStyle.Regular,
            topBarActions = topBarActions,
        )
    }
}

private enum class OnboardingTextStyle(val adventureSize: Int) {
    Regular(54),
    Large(76),
}

@Composable
private fun OnboardingStacked(
    state: OnboardingState,
    pagerState: PagerState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier,
    horizontalPadding: Dp,
    contentMaxWidth: Dp,
    textStyle: OnboardingTextStyle,
    topBarActions: @Composable RowScope.() -> Unit,
) {
    val pages = onboardingPages
    val bottomInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)

    Box(modifier = modifier.fillMaxSize().background(CarpathiansColors.BackgroundBottom)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { it },
        ) { index ->
            val pageOffset = pagerState.offsetOf(index)
            Box(Modifier.fillMaxSize()) {
                OnboardingPageImage(pages[index], pageOffset, Modifier.fillMaxSize())
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
                OnboardingPageText(
                    page = pages[index],
                    pageIndex = index,
                    pageCount = state.pageCount,
                    textStyle = textStyle,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .widthIn(max = contentMaxWidth)
                        .fillMaxWidth()
                        .windowInsetsPadding(bottomInsets)
                        .padding(start = horizontalPadding, end = horizontalPadding, bottom = 110.dp)
                        .graphicsLayer {
                            alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                            translationX = pageOffset * size.width * -0.2f
                        },
                )
            }
        }

        OnboardingTopBar(
            isLastPage = state.isLastPage,
            onIntent = onIntent,
            topBarActions = topBarActions,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .padding(horizontal = horizontalPadding - 8.dp, vertical = 12.dp),
        )

        OnboardingControls(
            state = state,
            onIntent = onIntent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = contentMaxWidth)
                .windowInsetsPadding(bottomInsets)
                .padding(horizontal = horizontalPadding, vertical = 24.dp),
        )
    }
}

@Composable
private fun OnboardingTwoPane(
    state: OnboardingState,
    pagerState: PagerState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier,
    topBarActions: @Composable RowScope.() -> Unit,
) {
    val pages = onboardingPages

    Row(modifier.fillMaxSize().background(CarpathiansBackgroundBrush)) {
        Box(Modifier.weight(1.1f).fillMaxHeight()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { it },
            ) { index ->
                Box(Modifier.fillMaxSize().clipToBounds()) {
                    OnboardingPageImage(pages[index], pagerState.offsetOf(index), Modifier.fillMaxSize())
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.horizontalGradient(
                                0.6f to Color.Transparent,
                                1f to CarpathiansColors.BackgroundTop.copy(alpha = 0.9f),
                            )
                        )
                    )
                }
            }
            BrandLogo(
                Modifier
                    .align(Alignment.TopStart)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Start))
                    .padding(horizontal = 32.dp, vertical = 24.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.End))
                .padding(horizontal = 40.dp, vertical = 16.dp),
        ) {
            OnboardingTopBar(
                isLastPage = state.isLastPage,
                showLogo = false,
                onIntent = onIntent,
                topBarActions = topBarActions,
            )

            AnimatedContent(
                targetState = state.currentPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) { page ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    OnboardingPageText(
                        page = pages[page],
                        pageIndex = page,
                        pageCount = state.pageCount,
                        textStyle = OnboardingTextStyle.Regular,
                        modifier = Modifier
                            .widthIn(max = 520.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 16.dp),
                    )
                }
            }

            OnboardingControls(state = state, onIntent = onIntent, modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

private fun PagerState.offsetOf(page: Int): Float = (currentPage - page) + currentPageOffsetFraction

@Composable
private fun OnboardingPageImage(page: OnboardingPage, pageOffset: Float, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(page.image),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.graphicsLayer {
            translationX = pageOffset * size.width * 0.5f
            val scale = 1.1f - 0.1f * (1f - pageOffset.absoluteValue.coerceIn(0f, 1f))
            scaleX = scale
            scaleY = scale
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OnboardingPageText(
    page: OnboardingPage,
    pageIndex: Int,
    pageCount: Int,
    textStyle: OnboardingTextStyle,
    modifier: Modifier = Modifier,
) {
    val isLarge = textStyle == OnboardingTextStyle.Large
    Column(modifier) {
        Text(
            text = stringResource(Res.string.onboarding_step, pageIndex + 1, pageCount),
            style = if (isLarge) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
            color = CarpathiansColors.Primary,
        )
        Spacer(Modifier.height(12.dp))
        if (page.showAdventureTitle) {
            AdventureTitle(fontSize = textStyle.adventureSize)
            Spacer(Modifier.height(8.dp))
        }
        FitText(
            text = stringResource(page.title),
            style = (if (isLarge) MaterialTheme.typography.displayMedium else MaterialTheme.typography.displaySmall)
                .copy(shadow = TextShadow),
            color = CarpathiansColors.White,
            minFontSize = 18.sp,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = TextShadow,
                fontSize = if (isLarge) 20.sp else 16.sp,
                lineHeight = if (isLarge) 30.sp else 24.sp,
            ),
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

@Composable
private fun OnboardingTopBar(
    isLastPage: Boolean,
    showLogo: Boolean = true,
    onIntent: (OnboardingIntent) -> Unit,
    topBarActions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showLogo) BrandLogo(Modifier.weight(1f).padding(end = 8.dp)) else Spacer(Modifier.weight(1f))
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
}

@Composable
private fun OnboardingControls(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PagerIndicator(pageCount = state.pageCount, currentPage = state.currentPage)
        Spacer(Modifier.weight(1f))
        AnimatedContent(
            targetState = state.isLastPage,
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
