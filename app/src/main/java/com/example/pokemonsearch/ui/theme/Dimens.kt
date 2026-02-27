package com.example.pokemonsearch.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 定义尺寸数据类
data class Dimens(
    // 间距
    val paddingExtraSmall: Dp = 4.dp,
    val paddingSmall6: Dp = 6.dp,
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 12.dp,
    val paddingLarge: Dp = 16.dp,
    val paddingLarge24: Dp = 24.dp,

    // 卡片圆角
    val cardCornerRadius: Dp = 8.dp,
    val tagCornerRadius: Dp = 50.dp, // 胶囊形状

    // 字体大小 (虽然通常建议放在 Typography 里，但这里也可以定义通用尺寸)
)

// 提供默认实例
val defaultDimens = Dimens()

// 创建 CompositionLocal，允许我们在 Composable 树中传递这些值
val LocalDimens = staticCompositionLocalOf { defaultDimens }