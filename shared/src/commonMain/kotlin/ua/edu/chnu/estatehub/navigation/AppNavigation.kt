package ua.edu.chnu.estatehub.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.viewmodel.koinViewModel
import ua.edu.chnu.estatehub.ui.auth.AuthRoute
import ua.edu.chnu.estatehub.ui.home.HomeRoute
import ua.edu.chnu.estatehub.ui.onboarding.OnboardingRoute

/**
 * Navigation 3: стек екранів (backStack) є звичайним списком у стані MainViewModel,
 * NavDisplay лише малює верхній елемент.
 */
@Composable
fun AppNavigation(
    backStack: List<Route>,
    onBack: () -> Unit,
    onNavigate: (Route) -> Unit,
) {
    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        // ViewModelStore на кожен елемент стека: koinViewModel() живе, поки екран у стеку
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<Route>(),
            rememberViewModelStoreNavEntryDecorator<Route>(),
        ),
        entryProvider = entryProvider<Route> {
            entry<Route.Onboarding> {
                OnboardingRoute(viewModel = koinViewModel(), onNavigate = onNavigate)
            }
            entry<Route.Auth> {
                AuthRoute(viewModel = koinViewModel(), onNavigate = onNavigate)
            }
            entry<Route.Home> {
                HomeRoute(
                    homeViewModel = koinViewModel(),
                    settingsViewModel = koinViewModel(),
                    onNavigate = onNavigate,
                )
            }
        },
    )
}
