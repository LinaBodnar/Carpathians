# Carpathians

Kotlin Multiplatform (Compose Multiplatform) клієнт для туристичних походів Карпатами.
Цілі: **Android**, **Desktop (JVM)**, **Web (Wasm / JS)**.

REST API: другий варіант — відкритий тестовий API [DummyJSON](https://dummyjson.com/docs/auth) (авторизація `/auth/login`, `/auth/refresh`, `/auth/me`).

## Лабораторна робота №4

| Завдання | Де реалізовано |
|---|---|
| 1. Новий KMP проєкт | `androidApp`, `desktopApp`, `webApp`, `shared` |
| 2. Дизайн додатка | прототип у [`project/`](./project) (HTML/CSS/JS) — з нього взято палітру, шрифти, компоненти |
| 3. Кастомна тема (кольори, шрифти) | [`shared/.../theme`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/theme) — `CarpathiansTheme`, `Color.kt`, `Type.kt`, `Shape.kt`; шрифти в `composeResources/font` |
| 4. Kermit + multiplatform-settings | [`logging/AppLogger.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/logging/AppLogger.kt), [`settings/AppSettings.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/settings/AppSettings.kt) |
| 5. Екран знайомства з Pager (5 слайдів) | [`ui/onboarding`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/ui/onboarding) — `HorizontalPager` |
| 6. Локалізація UA / EN | `composeResources/values` (EN), `composeResources/values-uk` (UA), перемикач мови [`locale/`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/locale) |

### Тема
* Кольори з `project/style.css`: зелений primary `#4CAF50` / `#2E7D32`, темний «гірський» градієнт `#1B2E2B → #050A09`, напівпрозорі «скляні» поверхні.
* Шрифти:
  * **DM Serif Text** — фірмовий шрифт дизайну (логотип, «ADVENTURE»);
  * **Playfair Display** — заголовки (DM Serif Text не має кирилиці, тому для українського тексту використано близький serif);
  * **Arimo** — основний текст (метрично сумісний з Arial з дизайну).
* Компоненти: `RouteButton` (.route-button), `GradientButton` (.btn-login-custom), `GlassChip`, `PagerIndicator`, `BrandLogo`.

### Налаштування (multiplatform-settings)
`AppSettings` зберігає: чи пройдено онбординг, останню сторінку онбордингу, обрану мову, кількість запусків.
Сховище: SharedPreferences (Android), `java.util.prefs` (Desktop), `localStorage` (Web).

### Логування (Kermit)
Логи запуску, навігації, перегортання слайдів, зміни мови та налаштувань. Тег `Carpathians`.

### Онбординг
5 слайдів, що повторюють секції дизайну: Hero → Маршрути → Галерея → Спорядження → Запис у похід.
Паралакс фото, анімований індикатор, кнопки «Пропустити» / «Далі» / «Почати». Після завершення відкривається головний екран (hero-секція),
з якого онбординг можна переглянути знову.

## Лабораторна робота №5

| Завдання | Де реалізовано |
|---|---|
| 1. Navigation 3 | `navigation3-ui` + `lifecycle-viewmodel-navigation3`; маршрути [`navigation/Route.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/navigation/Route.kt), граф [`navigation/AppNavigation.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/navigation/AppNavigation.kt) |
| 2. Koin | модулі та `initKoin()` у [`di/AppModule.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/di/AppModule.kt); запуск у `CarpathiansApplication` (Android) і `main()` (Desktop, Web) |
| 3. multiplatform-settings | [`settings/AppSettings.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/settings/AppSettings.kt) |
| 4. MVI онбордингу + use case | [`ui/onboarding`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/ui/onboarding) (`OnboardingContract`, `OnboardingViewModel`); [`domain/onboarding`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/domain/onboarding) (`CompleteOnboardingUseCase` …) → `OnboardingRepository` → [`data/onboarding/OnboardingRepositoryImpl`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/data/onboarding/OnboardingRepositoryImpl.kt) → `AppSettings` |
| 5–6. Екран авторизації, перехід з онбордингу | [`ui/auth/AuthScreen.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/ui/auth/AuthScreen.kt); «Почати» / «Пропустити» → `Route.Auth` |
| 7–8. ViewModel головного екрана, вибір стартового екрана | [`ui/main/MainViewModel.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/ui/main/MainViewModel.kt) + `IsOnboardingCompletedUseCase`: онбординг не пройдено → `Onboarding`, інакше → `Auth` |
| 9. Дизайн і MVI екрана авторизації | `AuthContract` / `AuthViewModel` |
| 9a. Валідація | [`domain/auth/validation`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/domain/auth/validation/CredentialsValidation.kt): імʼя — ≥ 3 символи, латиниця/цифри/`._-`; пароль — ≥ 6 символів, без пробілів |
| 9b. Дії клавіатури | імʼя: `ImeAction.Next` → фокус на пароль; пароль: `ImeAction.Done` → `AuthIntent.SignIn` (як кнопка «Увійти») |

### Архітектура
```
UI (Screen) ──Intent──▶ ViewModel ──▶ UseCase ──▶ Repository (interface) ──▶ RepositoryImpl ──▶ AppSettings
     ▲                     │
     └──── State / Effect ─┘
```
* `MviViewModel<State, Intent, Effect>` ([`core/mvi`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/core/mvi/MviViewModel.kt)) — стан у `StateFlow`, одноразові події (навігація, фокус поля, снекбар) через `Channel`.
* Кожен екран має `XxxRoute` (бере ViewModel з Koin, обробляє події) і stateless `XxxScreen`.
* Кожен запис Navigation 3 має власний `ViewModelStore` (`rememberViewModelStoreNavEntryDecorator`).
* Помилки валідації показуються після першої спроби входу, а далі оновлюються під час введення. Фокус переходить на перше поле з помилкою.
* Hero-екран з лабораторної №4 став основою екрана авторизації: той самий фон, ADVENTURE і хедер, плюс «скляна» форма входу (`.glass-login-modal` з дизайну).
* Back stack і сховище ViewModel створюються поза `AppEnvironment`, тому зміна мови не скидає ні поточний екран, ні введені дані.

## Лабораторна робота №6

| Завдання | Де реалізовано |
|---|---|
| 1. Ktor | `ktor-client-core`, `content-negotiation`, `auth`, `logging`; рушії: OkHttp (Android, Desktop), JS/fetch (Web) |
| 2. KSafe | `eu.anifantakis:ksafe`; екземпляр у `platformModule` (Android — з `Context`, Desktop/Web — з `appNamespace`) |
| 3. Мережевий стек авторизації | DTO — [`data/auth/remote/AuthDtos.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/data/auth/remote/AuthDtos.kt); запити — [`AuthApi`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/data/auth/remote/AuthApi.kt); репозиторій — [`AuthRepositoryImpl`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/data/auth/AuthRepositoryImpl.kt); use case — [`domain/auth/AuthUseCases.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/domain/auth/AuthUseCases.kt) (`LoginUseCase`, `GetCurrentUserUseCase`, `IsUserLoggedInUseCase`, `LogoutUseCase`, `ObserveSessionExpiredUseCase`) |
| 4. Безпечне збереження токенів | [`KSafeTokenStorage`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/data/auth/local/KSafeTokenStorage.kt) — AES-256-GCM; ключ в Android Keystore / сховищі ОС / WebCrypto |
| 5. Автоматичне оновлення токенів | [`HttpClientFactory.kt`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/data/auth/remote/HttpClientFactory.kt): Ktor `Auth { bearer { refreshTokens { … } } }` |
| 6. MVI екрана авторизації + use case | `AuthViewModel` викликає `LoginUseCase`; стан завантаження показується на кнопці «Увійти» |
| 7. Обробка помилок, снекбар через події | `AuthError` (`InvalidCredentials`, `Network`, `Server`, `SessionExpired`, `Unknown`) → `AuthEffect.ShowError` → `AppSnackbarHost` |
| 8. Домашня сторінка, перехід після входу | [`ui/home`](./shared/src/commonMain/kotlin/com/bodnar/carpathians/ui/home) — привітання, дані з `/auth/me`, «Оновити профіль», «Вийти» |
| 9. Навігація з урахуванням авторизації | `MainViewModel`: онбординг не пройдено → `Onboarding`; є токени → `Home`; інакше → `Auth`. Якщо сесія закінчилась — повернення на `Auth` |

### Як працює оновлення токенів
1. Після входу (`POST /auth/login`) пара `accessToken` / `refreshToken` зашифровано зберігається через KSafe.
2. Ktor додає `Authorization: Bearer <accessToken>` до захищених запитів (`/auth/me`).
3. Access-токен живе **1 хвилину** (`expiresInMins = 1`), щоб оновлення було видно в логах.
4. На відповідь `401` Ktor викликає `POST /auth/refresh`, зберігає нову пару токенів і **повторює** початковий запит. Для користувача це непомітно.
5. Якщо refresh-токен недійсний, токени видаляються, `SessionEvents` надсилає подію, і застосунок повертає користувача на екран входу зі снекбаром «Сесія закінчилась».

Демо-акаунт DummyJSON: **`emilys` / `emilyspass`**.

### Тести
`./gradlew :shared:jvmTest` (32 тести): use case'и, ViewModel'и (онбординг, вхід, домашня, головна), валідація, граф Koin,
а також мережевий стек на `MockEngine`: успішний вхід, неправильний пароль, помилка сервера, відсутність мережі,
автоматичне оновлення простроченого токена й завершення сесії, коли оновити не вдалося; збереження токенів у справжньому KSafe на JVM.

## Запуск

- Android: `./gradlew :androidApp:assembleDebug`
- Desktop: `./gradlew :desktopApp:run`
- Web (Wasm): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
- Web (JS): `./gradlew :webApp:jsBrowserDevelopmentRun`
- Тести: `./gradlew :shared:jvmTest`

Шрифти розповсюджуються за ліцензією SIL OFL 1.1 — див. [`licenses/`](./licenses).
