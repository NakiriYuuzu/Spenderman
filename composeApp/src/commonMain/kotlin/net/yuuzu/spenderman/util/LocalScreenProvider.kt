package net.yuuzu.spenderman.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp

internal val LocalScreenSize = staticCompositionLocalOf<ScreenSize> { error("No ScreenSize found") }

data class ScreenSize(val width: Dp, val height: Dp)

@Composable
internal expect fun getScreenWidthDp(): Dp

@Composable
internal expect fun getScreenHeightDp(): Dp