package net.yuuzu.spenderman.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal actual fun getScreenWidthDp(): Dp = LocalWindowInfo.current.containerSize.width.dp

@Composable
internal actual fun getScreenHeightDp(): Dp = LocalWindowInfo.current.containerSize.height.dp