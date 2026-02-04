package com.hisuperaman.wallety.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.ui.graphics.vector.ImageVector

enum class PaymentType(val label: String, val icon: ImageVector) {
    CASH("Cash", Icons.Default.Payments),
    CREDIT_CARD("Credit Card", Icons.Default.CreditCard),
    UPI("UPI", Icons.Default.Smartphone)
}
