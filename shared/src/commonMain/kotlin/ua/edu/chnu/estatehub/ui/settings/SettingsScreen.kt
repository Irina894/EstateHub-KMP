package ua.edu.chnu.estatehub.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ua.edu.chnu.estatehub.core.mvi.CollectEffects
import ua.edu.chnu.estatehub.domain.model.ThemeMode
import ua.edu.chnu.estatehub.locale.AppLanguage
import ua.edu.chnu.estatehub.navigation.Route
import ua.edu.chnu.estatehub.resources.*

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onNavigate: (Route) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is SettingsEffect.Navigate -> onNavigate(effect.route)
        }
    }
    SettingsScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAbout by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))

        SectionLabel(stringResource(Res.string.settings_language))
        Segmented(
            options = listOf(
                AppLanguage.UKRAINIAN to stringResource(Res.string.language_uk),
                AppLanguage.ENGLISH to stringResource(Res.string.language_en),
            ),
            selected = state.settings.language,
            onSelect = { onIntent(SettingsIntent.LanguageSelected(it)) },
        )

        Spacer(Modifier.height(8.dp))
        SectionLabel(stringResource(Res.string.settings_theme))
        Segmented(
            options = listOf(
                ThemeMode.LIGHT to stringResource(Res.string.theme_light),
                ThemeMode.DARK to stringResource(Res.string.theme_dark),
                ThemeMode.SYSTEM to stringResource(Res.string.theme_system),
            ),
            selected = state.settings.themeMode,
            onSelect = { onIntent(SettingsIntent.ThemeSelected(it)) },
        )

        Spacer(Modifier.height(8.dp))
        SettingsCard {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.settings_notifications),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Switch(
                    checked = state.settings.notificationsEnabled,
                    onCheckedChange = { onIntent(SettingsIntent.NotificationsToggled(it)) },
                )
            }
        }

        SettingsCard {
            ChevronRow(stringResource(Res.string.settings_show_onboarding)) {
                onIntent(SettingsIntent.ShowOnboardingAgain)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
            ChevronRow(stringResource(Res.string.settings_about)) { showAbout = true }
        }

        Spacer(Modifier.height(8.dp))
        SectionLabel(stringResource(Res.string.settings_account))
        SettingsCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = state.username ?: stringResource(Res.string.settings_guest),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                OutlinedButton(
                    onClick = { onIntent(SettingsIntent.SignOutClicked) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_logout),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(Res.string.settings_sign_out))
                }
            }
        }
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) { Text(stringResource(Res.string.dialog_ok)) }
            },
            title = { Text(stringResource(Res.string.app_name)) },
            text = { Text(stringResource(Res.string.settings_about_text)) },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        content()
    }
}

@Composable
private fun ChevronRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Власний сегментований перемикач (без залежності від експериментальних API Material3). */
@Composable
private fun <T> Segmented(
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    Row(
        modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { (value, label) ->
            val isSelected = value == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSelect(value) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
