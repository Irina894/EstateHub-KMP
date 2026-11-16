package ua.edu.chnu.estatehub.data.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import eu.anifantakis.lib.ksafe.KSafe

@Composable
actual fun rememberKSafe(): KSafe {
    val appContext = LocalContext.current.applicationContext // не Activity: уникаємо витоку пам'яті
    return remember { KSafe(appContext) }
}
