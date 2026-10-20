package ua.edu.chnu.estatehub.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ua.edu.chnu.estatehub.resources.Res
import ua.edu.chnu.estatehub.resources.home_dark_theme
import ua.edu.chnu.estatehub.resources.home_show_onboarding
import ua.edu.chnu.estatehub.resources.home_subtitle
import ua.edu.chnu.estatehub.resources.home_title

/**
 * Тимчасовий головний екран. У наступних лабораторних тут буде список оголошень з API EstateHub.
 * Показує дві речі з налаштувань користувача: темна тема й повторний показ onboarding.
 */
@Composable
fun HomeScreen(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onShowOnboardingAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(48.dp))
            Text(
                text = stringResource(Res.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 480.dp),
            )
            Spacer(Modifier.height(32.dp))

            Card(modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(Res.string.home_dark_theme),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onShowOnboardingAgain,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(Res.string.home_show_onboarding))
                    }
                }
            }
        }
    }
}
