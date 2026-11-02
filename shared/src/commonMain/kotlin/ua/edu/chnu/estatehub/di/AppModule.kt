package ua.edu.chnu.estatehub.di

import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ua.edu.chnu.estatehub.data.AppSettingsRepositoryImpl
import ua.edu.chnu.estatehub.data.FavoritesRepositoryImpl
import ua.edu.chnu.estatehub.data.InMemoryListingsRepository
import ua.edu.chnu.estatehub.data.LocalAuthRepository
import ua.edu.chnu.estatehub.data.OnboardingRepositoryImpl
import ua.edu.chnu.estatehub.domain.repository.AppSettingsRepository
import ua.edu.chnu.estatehub.domain.repository.AuthRepository
import ua.edu.chnu.estatehub.domain.repository.FavoritesRepository
import ua.edu.chnu.estatehub.domain.repository.ListingsRepository
import ua.edu.chnu.estatehub.domain.repository.OnboardingRepository
import ua.edu.chnu.estatehub.domain.usecase.GetCurrentUserUseCase
import ua.edu.chnu.estatehub.domain.usecase.GetListingByIdUseCase
import ua.edu.chnu.estatehub.domain.usecase.GetListingsUseCase
import ua.edu.chnu.estatehub.domain.usecase.IsOnboardingCompletedUseCase
import ua.edu.chnu.estatehub.domain.usecase.IsSignedInUseCase
import ua.edu.chnu.estatehub.domain.usecase.ObserveAppSettingsUseCase
import ua.edu.chnu.estatehub.domain.usecase.ObserveFavoritesUseCase
import ua.edu.chnu.estatehub.domain.usecase.SaveOnboardingResultUseCase
import ua.edu.chnu.estatehub.domain.usecase.SignInUseCase
import ua.edu.chnu.estatehub.domain.usecase.SignOutUseCase
import ua.edu.chnu.estatehub.domain.usecase.ToggleFavoriteUseCase
import ua.edu.chnu.estatehub.domain.usecase.UpdateLanguageUseCase
import ua.edu.chnu.estatehub.domain.usecase.UpdateNotificationsUseCase
import ua.edu.chnu.estatehub.domain.usecase.UpdateThemeModeUseCase
import ua.edu.chnu.estatehub.domain.usecase.ValidatePasswordUseCase
import ua.edu.chnu.estatehub.domain.usecase.ValidateUsernameUseCase
import ua.edu.chnu.estatehub.ui.auth.AuthViewModel
import ua.edu.chnu.estatehub.ui.details.DetailsViewModel
import ua.edu.chnu.estatehub.ui.home.HomeViewModel
import ua.edu.chnu.estatehub.ui.main.MainViewModel
import ua.edu.chnu.estatehub.ui.onboarding.OnboardingViewModel
import ua.edu.chnu.estatehub.ui.settings.SettingsViewModel

/** Koin: єдиний граф залежностей (DI). Settings, репозиторії й use cases створюються тут. */
val appModule = module {
    // --- дані
    single<Settings> { Settings() }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single<AuthRepository> { LocalAuthRepository(get()) }
    single<AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }
    single<ListingsRepository> { InMemoryListingsRepository() }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }

    // --- use cases
    factory { IsOnboardingCompletedUseCase(get()) }
    factory { SaveOnboardingResultUseCase(get()) }
    factory { IsSignedInUseCase(get()) }
    factory { SignInUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { ValidateUsernameUseCase() }
    factory { ValidatePasswordUseCase() }
    factory { ObserveAppSettingsUseCase(get()) }
    factory { UpdateThemeModeUseCase(get()) }
    factory { UpdateLanguageUseCase(get()) }
    factory { UpdateNotificationsUseCase(get()) }
    factory { GetListingsUseCase(get()) }
    factory { GetListingByIdUseCase(get()) }
    factory { ObserveFavoritesUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }

    // --- ViewModels
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { DetailsViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
