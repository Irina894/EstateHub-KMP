This is a Kotlin Multiplatform project targeting Android, Desktop (JVM).

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- Desktop app:
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Standard run: `./gradlew :desktopApp:run`

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- Desktop tests: `./gradlew :shared:jvmTest`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
## Лабораторна 5: навігація, DI, налаштування, MVI, екран входу

Продовження лаби 4. Додано Navigation 3, Koin, multiplatform-settings, MVI для екранів, онбординг із збереженням результату, екран входу з валідацією, головний екран із нижньою навігацією, екран деталей.

### Архітектура

```
ui (Compose)  ->  ViewModel (MVI: State / Intent / Effect)  ->  use case  ->  repository (interface)  <-  data (settings / in-memory)
```

| Шар | Пакет | Що містить |
|-----|-------|------------|
| UI | `ui/*` | Екрани Compose (stateless), `*Route` підписується на ViewModel |
| MVI | `core/mvi`, `ui/*/*Contract.kt` | `MviViewModel<State, Intent, Effect>`, один стан і одноразові ефекти (навігація) |
| Domain | `domain/*` | Моделі, інтерфейси репозиторіїв, use case-и (валідація, онбординг, вхід, обрані, оголошення) |
| Data | `data/*` | Репозиторії на `multiplatform-settings` (онбординг, вхід, налаштування, обрані), тимчасові оголошення |
| DI | `di/AppModule.kt` | Koin: репозиторії, use case-и, ViewModel-и |
| Навігація | `navigation/*` | Navigation 3: `Route` (Onboarding, Auth, Home, Details) і `NavDisplay` |

### Логіка першого екрана
`MainViewModel` при старті перевіряє use case-ами `IsOnboardingCompletedUseCase` та `IsSignedInUseCase` і вибирає стартовий екран: онбординг, вхід або головний. Стек навігації лежить у стані `MainViewModel`, тож «Назад» і зміна мови не гублять екран.

### MVI
- **Onboarding**: інтенти `NextClicked`, `SkipClicked`, `PageChanged`; після завершення `SaveOnboardingResultUseCase` зберігає результат, ефект `Navigate` веде на вхід.
- **Auth**: валідація імені (3-32 символи, літери/цифри/`._-`) і пароля (від 8 символів, літера й цифра). Клавіатура: Next у полі імені переходить до пароля, Done у полі пароля виконує вхід так само, як кнопка «Увійти».
- Вхід поки локальний (демо), інтерфейс `AuthRepository` готовий до підключення API.

### Запуск
- Desktop: `./gradlew :desktopApp:run`
- Android: запуск конфігурації `androidApp`
- Тести: `./gradlew :shared:jvmTest`
