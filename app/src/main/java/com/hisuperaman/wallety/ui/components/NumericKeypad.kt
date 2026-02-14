package com.hisuperaman.wallety.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.ui.theme.SoftBlue
import com.hisuperaman.wallety.ui.theme.SoftGreen
import com.hisuperaman.wallety.ui.theme.SoftPeach

@Composable
fun KeypadButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    text: String? = "",
    imageVector: ImageVector? = null,
    longPressImageVector: ImageVector? = null,
    bgColor: Color? = null,
    outline: Boolean = true
) {
    val contentColor =
        if (bgColor == null) (if (outline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary) else Color.Black
    var isLongPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (onLongClick!=null && isLongPressed) 1.6f else 1f)

    ActionButtonBase(
        onClick = onClick,
        modifier = modifier,
        outline = outline,
        roundedCornerPercentage = 25,
        bgColor = bgColor,
        onLongPress = {
            isLongPressed = true
        },
        onLongPressReleased = {
            isLongPressed = false
            onLongClick?.invoke()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (imageVector != null) {
                Icon(
                    imageVector = if (isLongPressed && longPressImageVector != null) longPressImageVector else imageVector,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier
                        .scale(scale)
                )
                if (text != null) Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacer_sm)))
            }
            if (text != null) {
                Text(
                    text = text,
                    color = contentColor,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.W400
                )
            }
        }
    }
}

data class GridItem(
    val row: Int,
    val column: Int,
    val rowSpan: Int = 1,
    val colSpan: Int = 1,
    val content: @Composable () -> Unit
)


@Composable
fun FixedSpanGrid(
    columns: Int,
    rows: Int,
    spacing: Dp,
    modifier: Modifier = Modifier,
    items: List<GridItem>
) {
    val density = LocalDensity.current
    val gapPx = with(density) { spacing.roundToPx() }

    Layout(
        modifier = modifier,
        content = { items.forEach { it.content() } }
    ) { measurables, constraints ->

        val cellWidth =
            (constraints.maxWidth - gapPx * (columns - 1)) / columns
        val cellHeight =
            (constraints.maxHeight - gapPx * (rows - 1)) / rows

        val placeables = measurables.mapIndexed { i, measurable ->
            val item = items[i]
            val w =
                cellWidth * item.colSpan + gapPx * (item.colSpan - 1)
            val h =
                cellHeight * item.rowSpan + gapPx * (item.rowSpan - 1)

            measurable.measure(Constraints.fixed(w, h)) to item
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { (p, item) ->
                p.place(
                    x = item.column * (cellWidth + gapPx),
                    y = item.row * (cellHeight + gapPx)
                )
            }
        }
    }
}


@Composable
fun NumericKeypad(
    onKeyClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onDatePickerClick: () -> Unit,
    onCommentClick: () -> Unit,
    onSaveClick: () -> Unit,
    showDeleteKey: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FixedSpanGrid(
        columns = 4,
        rows = 4,
        spacing = 4.dp,
        modifier = modifier.fillMaxSize(),
        items = listOf(
            GridItem(0, 0) {
                KeypadButton(
                    text = "1",
                    onClick = { onKeyClick("1") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(0, 1) {
                KeypadButton(
                    text = "2",
                    onClick = { onKeyClick("2") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(0, 2) {
                KeypadButton(
                    text = "3",
                    onClick = { onKeyClick("3") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(0, 3) {
                KeypadButton(
                    imageVector = Icons.Default.Clear,
                    longPressImageVector = if (showDeleteKey) Icons.Default.DeleteOutline else null,
                    onClick = { onBackspaceClick() },
                    onLongClick = if (showDeleteKey) ({ onDeleteClick() }) else null,
                    bgColor = SoftPeach,
                    modifier = Modifier.fillMaxWidth()
                )
            },

            GridItem(1, 0) {
                KeypadButton(
                    text = "4",
                    onClick = { onKeyClick("4") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(1, 1) {
                KeypadButton(
                    text = "5",
                    onClick = { onKeyClick("5") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(1, 2) {
                KeypadButton(
                    text = "6",
                    onClick = { onKeyClick("6") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(1, 3) {
                KeypadButton(
                    imageVector = Icons.Default.DateRange,
                    onClick = { onDatePickerClick() },
                    bgColor = SoftBlue,
                    modifier = Modifier.fillMaxWidth()
                )
            },

            GridItem(2, 0) {
                KeypadButton(
                    text = "7",
                    onClick = { onKeyClick("7") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(2, 1) {
                KeypadButton(
                    text = "8",
                    onClick = { onKeyClick("8") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(2, 2) {
                KeypadButton(
                    text = "9",
                    onClick = { onKeyClick("9") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(2, 3, rowSpan = 2) {
                KeypadButton(
                    imageVector = Icons.Default.Check,
                    onClick = { onSaveClick() },
                    outline = false,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(3, 0) {
                KeypadButton(
                    imageVector = Icons.Default.AddComment,
                    onClick = { onCommentClick() },
                    modifier = Modifier.fillMaxWidth(),
                    bgColor = SoftGreen
                )
            },
            GridItem(3, 1) {
                KeypadButton(
                    text = "0",
                    onClick = { onKeyClick("0") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            GridItem(3, 2) {
                KeypadButton(
                    text = ".",
                    onClick = { onKeyClick(".") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    )

}