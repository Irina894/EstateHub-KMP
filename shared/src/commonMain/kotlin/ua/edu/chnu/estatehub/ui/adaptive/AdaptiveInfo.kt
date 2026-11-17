package ua.edu.chnu.estatehub.ui.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

enum class WidthClass { Compact, Medium, Expanded }

/**
 * Клас розміру вікна, зведений до того, що потрібно екранам.
 * Межі беруться з WindowSizeClass (Material): ширина 600 dp і 840 dp, висота 480 dp.
 */
data class AdaptiveInfo(
    val width: WidthClass,
    val isHeightCompact: Boolean,
) {
    /** Планшет, desktop або телефон у ландшафті: екран ділиться на дві панелі. */
    val useTwoPane: Boolean get() = width != WidthClass.Compact || isHeightCompact
}

@Composable
fun rememberAdaptiveInfo(): AdaptiveInfo {
    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val width = when {
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> WidthClass.Expanded
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> WidthClass.Medium
        else -> WidthClass.Compact
    }
    val heightCompact = !sizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
    return AdaptiveInfo(width, heightCompact)
}
