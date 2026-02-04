package com.hisuperaman.wallety.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.hisuperaman.wallety.ui.theme.AdaptiveGreen
import com.hisuperaman.wallety.ui.theme.AdaptiveRed
import com.hisuperaman.wallety.ui.theme.SoftBlue
import com.hisuperaman.wallety.ui.theme.SoftGreen
import com.hisuperaman.wallety.ui.theme.SoftPeach
import com.hisuperaman.wallety.ui.theme.SoftYellow

enum class Category(val label: String, val icon: ImageVector, val color: Color) {
    INCOME("Income", Icons.Default.AttachMoney, Color(0xFF2E7D32)),
    FOOD("Food", Icons.Default.Fastfood, Color(0xFFF9A825)),
    SHOPPING("Shopping", Icons.Default.ShoppingCart, Color(0xFFD32F2F)),
    TRAVEL("Travel", Icons.Default.Flight, Color(0xFF1976D2)),
    BILLS("Bills", Icons.Default.Receipt, Color(0xFF7B1FA2))
}
