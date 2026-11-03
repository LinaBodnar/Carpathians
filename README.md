# Carpathians

Kotlin Multiplatform (Compose Multiplatform) клієнт для туристичних походів Карпатами.
Цілі: **Android**, **Desktop (JVM)**, **Web (Wasm / JS)**.

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

## Запуск

- Android: `./gradlew :androidApp:assembleDebug`
- Desktop: `./gradlew :desktopApp:run`
- Web (Wasm): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
- Web (JS): `./gradlew :webApp:jsBrowserDevelopmentRun`
- Тести: `./gradlew :shared:jvmTest`

Шрифти розповсюджуються за ліцензією SIL OFL 1.1 — див. [`licenses/`](./licenses).
