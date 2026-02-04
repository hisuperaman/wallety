package com.hisuperaman.wallety.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import com.hisuperaman.wallety.R

@Composable
fun SelectMenu(
    modifier: Modifier = Modifier,
    options: List<String>? = null,
    optionsWithIcon: List<Pair<String, ImageVector>>? = null,
    selected: String,
    onSelect: (String) -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.surface,
    imageVector: ImageVector? = null,
    contentColor: Color = MaterialTheme.colorScheme.onBackground
) {
    var expanded by remember { mutableStateOf(false) }
    var surfaceWidth by remember { mutableIntStateOf(0) }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f)

    Surface(
        color = color,
        contentColor = contentColor,
        shape = RoundedCornerShape(50),
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        val released = tryAwaitRelease()
                        pressed = false
                        if (released) expanded = true
                    }
                )
            }
            .onGloballyPositioned { coordinates ->
                surfaceWidth = coordinates.size.width
            },
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.padding_md),
                        end = dimensionResource(R.dimen.padding_regular),
                        top = dimensionResource(R.dimen.padding_sm),
                        bottom = dimensionResource(R.dimen.padding_sm)
                    )
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (imageVector != null) {
                        Icon(
                            imageVector = imageVector,
                            contentDescription = null
                        )
                    }
                    Text(
                        text = selected,
                        style = textStyle
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { surfaceWidth.toDp() })
            ) {
                if (optionsWithIcon != null) {
                    optionsWithIcon.forEach { (label, icon) ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null
                                    )
                                    Text(label)
                                }
                            },
                            onClick = {
                                onSelect(label)
                                expanded = false
                            }
                        )
                    }
                }
                else options?.forEach {
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(it)
                            }
                        },
                        onClick = {
                            onSelect(it)
                            expanded = false
                        }
                    )
                }

            }
        }
    }
}