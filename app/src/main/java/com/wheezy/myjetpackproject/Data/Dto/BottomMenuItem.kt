package com.wheezy.myjetpackproject.Data.Dto

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomMenuItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
