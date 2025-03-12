package net.yuuzu.spenderman.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable
internal actual fun getScreenWidthDp(): Dp = LocalScreenSize.current.width

@Composable
internal actual fun getScreenHeightDp(): Dp = LocalScreenSize.current.height