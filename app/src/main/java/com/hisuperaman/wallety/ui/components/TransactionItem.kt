package com.hisuperaman.wallety.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.data.model.Transaction


fun Long.toFormattedDate(pattern: String = "dd MMM yyyy"): String {
    val sdf = java.text.SimpleDateFormat(pattern, java.util.Locale.getDefault())
    return sdf.format(java.util.Date(this))
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                onClick = { onClick() }
            )
            .padding(dimensionResource(R.dimen.padding_regular))
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_regular)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape
            ) {
                Icon(
                    imageVector = transaction.category.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_regular))
                )
            }
            Column{
                Text(
                    text = transaction.category.label,
                    style = MaterialTheme.typography.titleMedium
                )
                if (transaction.comment!="") {
                    Text(
                        text = transaction.comment,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            MoneyText(
                amount = transaction.amount,
                transactionType = transaction.type
            )
            Text(
                text = transaction.date.toFormattedDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}