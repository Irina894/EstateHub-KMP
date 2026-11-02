package ua.edu.chnu.estatehub.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Палітра зі Stitch (дизайн-система "Domivka Architectural Realty"):
// primary #2F5D50 (глибокий зелений), secondary #B5523B (теракота),
// tertiary #E6E2DA (теплий нейтральний), neutral #1C1B19. Світла й темна теми.
// ---------------------------------------------------------------------------

// Light
private val LightPrimary = Color(0xFF2F5D50)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFD3E8DF)
private val LightOnPrimaryContainer = Color(0xFF0B2A21)
private val LightSecondary = Color(0xFFB5523B)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightSecondaryContainer = Color(0xFFFFDBD1)
private val LightOnSecondaryContainer = Color(0xFF3B0E04)
private val LightBackground = Color(0xFFFDF8F5)
private val LightOnBackground = Color(0xFF26352F)
private val LightSurface = Color(0xFFFDF8F5)
private val LightOnSurface = Color(0xFF26352F)
private val LightSurfaceVariant = Color(0xFFF2EDE9)
private val LightOnSurfaceVariant = Color(0xFF62706A)
private val LightOutline = Color(0xFF7B7A73)
private val LightOutlineVariant = Color(0xFFE6E2DA)

// Dark
private val DarkPrimary = Color(0xFF8DC7B4)
private val DarkOnPrimary = Color(0xFF00382B)
private val DarkPrimaryContainer = Color(0xFF2F5D50)
private val DarkOnPrimaryContainer = Color(0xFFD3E8DF)
private val DarkSecondary = Color(0xFFC8897F)
private val DarkOnSecondary = Color(0xFF3B0E04)
private val DarkSecondaryContainer = Color(0xFF7A3524)
private val DarkOnSecondaryContainer = Color(0xFFFFDBD1)
private val DarkBackground = Color(0xFF141412)
private val DarkOnBackground = Color(0xFFE9E5DD)
private val DarkSurface = Color(0xFF141412)
private val DarkOnSurface = Color(0xFFE6E2DA)
private val DarkSurfaceVariant = Color(0xFF20201D)
private val DarkOnSurfaceVariant = Color(0xFFC9C5BC)
private val DarkOutline = Color(0xFF93908A)
private val DarkOutlineVariant = Color(0xFF3A3935)

val EstateHubLightColors: ColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
)

val EstateHubDarkColors: ColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
)
