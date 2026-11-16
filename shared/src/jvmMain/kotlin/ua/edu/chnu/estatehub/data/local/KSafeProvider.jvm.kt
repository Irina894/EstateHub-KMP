package ua.edu.chnu.estatehub.data.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import eu.anifantakis.lib.ksafe.KSafe

@Composable
actual fun rememberKSafe(): KSafe = remember { KSafe(fileName = "estatehub") }
