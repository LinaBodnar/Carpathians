package com.bodnar.carpathians.ui.onboarding

import androidx.compose.ui.graphics.Color
import carpathians.shared.generated.resources.*
import com.bodnar.carpathians.theme.CarpathiansColors
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/** Чіп на слайді: текст + необов'язкова кольорова позначка. */
data class OnboardingChip(
    val text: StringResource,
    val dotColor: Color? = null,
)

/** Опис одного слайда екрана знайомства. */
data class OnboardingPage(
    val image: DrawableResource,
    val title: StringResource,
    val description: StringResource,
    val chips: List<OnboardingChip> = emptyList(),
    val showAdventureTitle: Boolean = false,
)

/** 5 слайдів, що повторюють секції дизайну: Hero, Маршрути, Галерея, Спорядження, Запис. */
val onboardingPages: List<OnboardingPage> = listOf(
    OnboardingPage(
        image = Res.drawable.onboarding_welcome,
        title = Res.string.onboarding_welcome_title,
        description = Res.string.onboarding_welcome_text,
        showAdventureTitle = true,
    ),
    OnboardingPage(
        image = Res.drawable.onboarding_routes,
        title = Res.string.onboarding_routes_title,
        description = Res.string.onboarding_routes_text,
        chips = listOf(
            Res.string.route_shpitsi,
            Res.string.route_pip_ivan,
            Res.string.route_hoverla,
            Res.string.route_svydovets,
            Res.string.route_gorgany,
            Res.string.route_kukul,
            Res.string.route_brebeneskul,
            Res.string.route_gutin,
        ).map { OnboardingChip(it) },
    ),
    OnboardingPage(
        image = Res.drawable.onboarding_gallery,
        title = Res.string.onboarding_gallery_title,
        description = Res.string.onboarding_gallery_text,
    ),
    OnboardingPage(
        image = Res.drawable.onboarding_gear,
        title = Res.string.onboarding_gear_title,
        description = Res.string.onboarding_gear_text,
        chips = listOf(
            OnboardingChip(Res.string.gear_backpack, CarpathiansColors.Primary),
            OnboardingChip(Res.string.gear_water, CarpathiansColors.Blue),
            OnboardingChip(Res.string.gear_raincoat, CarpathiansColors.Info),
            OnboardingChip(Res.string.gear_shoes, CarpathiansColors.Warning),
            OnboardingChip(Res.string.gear_first_aid, CarpathiansColors.Danger),
            OnboardingChip(Res.string.gear_snack, CarpathiansColors.White),
        ),
    ),
    OnboardingPage(
        image = Res.drawable.onboarding_join,
        title = Res.string.onboarding_join_title,
        description = Res.string.onboarding_join_text,
    ),
)
