package com.hisuperaman.wallety.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.hisuperaman.wallety.data.model.TransactionType

@Composable
fun TransactionTypeFilterItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val color =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f)

    Surface(
        color = color,
        shape = RoundedCornerShape(50),
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        val released = tryAwaitRelease()
                        pressed = false
                        if (released) onClick()
                    }
                )
            }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun TransactionTypeFilter(
    selected: TransactionType?,
    onSelect: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            TransactionTypeFilterItem(
                label = TransactionType.INCOME.label,
                selected = selected == TransactionType.INCOME,
                modifier = Modifier.weight(1f),
                onClick = { onSelect(TransactionType.INCOME) }
            )
            TransactionTypeFilterItem(
                label = TransactionType.EXPENSE.label,
                selected = selected == TransactionType.EXPENSE,
                modifier = Modifier.weight(1f),
                onClick = { onSelect(TransactionType.EXPENSE) }
            )
        }
    }
}