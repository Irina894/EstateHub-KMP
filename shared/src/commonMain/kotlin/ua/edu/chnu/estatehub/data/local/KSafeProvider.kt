package ua.edu.chnu.estatehub.data.local

import androidx.compose.runtime.Composable
import eu.anifantakis.lib.ksafe.KSafe

/** KSafe на Android потребує Context, на desktop - ні, тому створення платформне. */
@Composable
expect fun rememberKSafe(): KSafe
