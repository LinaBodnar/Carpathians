package com.bodnar.carpathians.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.bodnar.carpathians.logging.AppLogger
import com.bodnar.carpathians.ui.auth.AuthRoute
import com.bodnar.carpathians.ui.home.HomeRoute
import com.bodnar.carpathians.ui.routes.RouteDetailsRoute
import com.bodnar.carpathians.ui.routes.RoutesRoute
import com.bodnar.carpathians.ui.onboarding.OnboardingRoute

private val log = AppLogger.withTag("Navigation")

/**
 * Декоратор, що дає кожному екрану власне сховище ViewModel
 * (очищається, коли екран прибрано зі стеку). Створюється вище за AppEnvironment,
 * щоб зміна мови не скидала ViewModel і введені дані.
 */
@Composable
fun rememberAppViewModelDecorator(): NavEntryDecorator<NavKey> = rememberViewModelStoreNavEntryDecorator()

/** Граф навігації застосунку на Navigation 3. */
@Composable
fun AppNavDisplay(
    backStack: NavBackStack<NavKey>,
    viewModelDecorator: NavEntryDecorator<NavKey>,
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.goBack() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            viewModelDecorator,
        ),
        entryProvider = entryProvider {
            entry<Route.Onboarding> {
                OnboardingRoute(onNavigateToAuth = { backStack.replaceAll(Route.Auth) })
            }
            entry<Route.Auth> {
                AuthRoute(onNavigateToHome = { backStack.replaceAll(Route.Home) })
            }
            entry<Route.Home> {
                HomeRoute(
                    onNavigateToAuth = { backStack.replaceAll(Route.Auth) },
                    onNavigateToRoutes = { backStack.navigate(Route.Routes) },
                )
            }
            entry<Route.Routes> {
                RoutesRoute(
                    onBack = { backStack.goBack() },
                    onOpenRoute = { id -> backStack.navigate(Route.RouteDetails(id)) },
                )
            }
            entry<Route.RouteDetails> { key ->
                RouteDetailsRoute(routeId = key.routeId, onBack = { backStack.goBack() })
            }
        },
    )
}

/** Відкриває екран поверх поточного (з можливістю повернутися "назад"). */
fun NavBackStack<NavKey>.navigate(route: Route) {
    if (lastOrNull() == route) return
    log.i { "Navigate: ${lastOrNull()} -> $route" }
    add(route)
}

/** Повертає на попередній екран (останній екран у стеку не прибирається). */
fun NavBackStack<NavKey>.goBack() {
    if (size > 1) removeAt(lastIndex)
}

/** Замінює весь стек одним екраном (повернутися "назад" на попередній не можна). */
fun NavBackStack<NavKey>.replaceAll(route: Route) {
    if (size == 1 && lastOrNull() == route) return
    log.i { "Navigate: ${lastOrNull()} -> $route" }
    add(route)
    removeAll { it != route }
}
