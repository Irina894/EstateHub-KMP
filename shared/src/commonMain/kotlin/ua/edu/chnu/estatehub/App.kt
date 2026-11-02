package ua.edu.chnu.estatehub

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import ua.edu.chnu.estatehub.di.appModule
import ua.edu.chnu.estatehub.domain.model.ThemeMode
import ua.edu.chnu.estatehub.locale.ProvideAppLanguage
import ua.edu.chnu.estatehub.navigation.AppNavigation
import ua.edu.chnu.estatehub.ui.main.MainIntent
import ua.edu.chnu.estatehub.ui.main.MainViewModel
import ua.edu.chnu.estatehub.ui.theme.EstateHubTheme

// Без @Preview: Koin і Settings() потребують реального середовища.
@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        AppRoot()
    }
}

@Composable
private fun AppRoot(viewModel: MainViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val settings = state.settings

    val darkTheme = when (settings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    ProvideAppLanguage(settings.language) {
        EstateHubTheme(darkTheme = darkTheme) {
            if (state.backStack.isEmpty()) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
            } else {
                AppNavigation(
                    backStack = state.backStack,
                    onBack = { viewModel.onIntent(MainIntent.Back) },
                    onNavigate = { viewModel.onIntent(MainIntent.ReplaceAll(it)) },
                    onPush = { viewModel.onIntent(MainIntent.Push(it)) },
                )
            }
        }
    }
}
