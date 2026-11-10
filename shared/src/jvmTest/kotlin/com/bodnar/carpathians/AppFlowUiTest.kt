package com.bodnar.carpathians

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.bodnar.carpathians.di.appModules
import com.bodnar.carpathians.domain.auth.AuthRepository
import com.bodnar.carpathians.domain.auth.TokenStorage
import com.bodnar.carpathians.locale.customAppLocale
import com.bodnar.carpathians.settings.AppSettings
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test

/**
 * Наскрізний UI-тест: проходить увесь застосунок кліками
 * (онбординг → реєстрація → вхід → головна → маршрути → деталі → запис у похід)
 * на вузькому телефоні, звичайному телефоні та широкому вікні.
 * Знімки кожного кроку зберігаються в build/screenshots.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class, ExperimentalComposeUiApi::class)
class AppFlowUiTest {

    @Test
    fun narrowPhone() = runFlow("320", width = 320, height = 640)

    @Test
    fun phone() = runFlow("360", width = 360, height = 760)

    @Test
    fun desktopWindow() = runFlow("1000", width = 1000, height = 720)

    private fun runFlow(name: String, width: Int, height: Int) {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        startKoin {
            allowOverride(true)
            modules(
                appModules + module {
                    single { AppSettings(MapSettings()) }
                    single<TokenStorage> { InMemoryTokenStorage() }
                    single<AuthRepository> { FakeAuthRepository() }
                }
            )
        }
        customAppLocale = "uk"
        try {
            runDesktopComposeUiTest(width = width, height = height) {
                setContent { App() }
                flow(name)
            }
        } finally {
            stopKoin()
            Dispatchers.resetMain()
        }
    }

    private fun ComposeUiTest.flow(name: String) {
        // 1. Онбординг
        onNodeWithText("Ласкаво просимо в Карпати").assertIsDisplayed()
        shot(name, "01-onboarding")
        onNodeWithText("Далі").performClick()
        waitForIdle()
        shot(name, "02-onboarding-routes")
        onNodeWithText("Пропустити").performClick()

        // 2. Вхід → вкладка "Реєстрація"
        waitUntilText("Увійти")
        shot(name, "03-sign-in")
        onNodeWithText("Реєстрація").performClick()
        waitUntilText("Зареєструватися")
        onNodeWithText("Зареєструватися").performScrollTo().performClick()
        waitUntilText("Обовʼязкове поле")
        shot(name, "04-sign-up-errors")

        onNodeWithText("Імʼя").performScrollTo().performTextInput("Ліна")
        onNodeWithText("Імʼя користувача").performTextInput("lina")
        onNodeWithText("Email").performTextInput("lina@mail.com")
        onNodeWithText("Пароль").performTextInput("secret1")
        onNodeWithText("Повторіть пароль").performTextInput("secret1")
        waitForIdle()
        shot(name, "05-sign-up-filled")
        onNodeWithText("Зареєструватися").performScrollTo().performClick()

        // 3. Після реєстрації — знову вкладка входу з підставленим іменем
        waitUntilText("Увійти")
        shot(name, "06-registered")
        onNodeWithText("Пароль").performTextInput("emilyspass")
        onNodeWithText("✕").performClick()
        waitForIdle()
        onNodeWithText("Увійти").performScrollTo().performClick()

        // 4. Головна
        waitUntilText("ОБРАТИ МАРШРУТ")
        shot(name, "07-home")
        onNodeWithText("ОБРАТИ МАРШРУТ").performScrollTo().performClick()

        // 5. Маршрути + фільтр
        waitUntilText("ШПИЦІ")
        shot(name, "08-routes")
        onNodeWithText("Легкий").performClick()
        waitUntilText("КУКУЛ")
        shot(name, "09-routes-easy")
        onNodeWithText("КУКУЛ").performClick()

        // 6. Деталі маршруту
        waitUntilText("ЗАПИСАТИСЯ В ПОХІД")
        shot(name, "10-route-details")
        onNodeWithText("ЗАПИСАТИСЯ В ПОХІД").performClick()

        // 7. Запис у похід
        waitUntilText("ЗАБРОНЮВАТИ")
        onNodeWithText("ЗАБРОНЮВАТИ").performClick()
        waitUntilText("Обовʼязкове поле")
        shot(name, "11-booking-errors")
        onNodeWithText("Імʼя").performTextInput("Ліна")
        onNodeWithText("Телефон").performTextInput("+380671234567")
        onNodeWithText("ЗАБРОНЮВАТИ").performClick()
        waitUntilText("Заявку прийнято! Ми звʼяжемося з вами найближчим часом.")
        shot(name, "12-booking-accepted")

        // 8. Назад на головну та вихід
        onNodeWithText("Кукул").assertIsDisplayed()
    }

    private fun ComposeUiTest.waitUntilText(text: String) {
        waitUntil(timeoutMillis = 5_000) {
            onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
        waitForIdle()
    }

    /** Знімок верхнього шару (екран або діалог поверх нього). */
    private fun ComposeUiTest.shot(name: String, step: String) {
        waitForIdle()
        val dir = File("build/screenshots/$name").apply { mkdirs() }
        val roots = onAllNodes(isRoot())
        val image = (if (roots.fetchSemanticsNodes().size > 1) roots.onLast() else roots.onFirst()).captureToImage()
        ImageIO.write(image.toAwtImage(), "png", File(dir, "$step.png"))
    }
}
