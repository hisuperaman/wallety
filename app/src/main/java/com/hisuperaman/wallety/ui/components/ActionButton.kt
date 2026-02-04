package com.hisuperaman.wallety.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.hisuperaman.wallety.R


@Composable
fun ActionButtonBase(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    outline: Boolean = false,
    roundedCornerPercentage: Int = 50,
    bgColor: Color? = null,
    content: @Composable () -> Unit
) {
    val backgroundColor = bgColor
        ?: if (!outline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f)

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(roundedCornerPercentage))
            .background(backgroundColor)
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
            .padding(
                vertical = dimensionResource(R.dimen.padding_regular),
                horizontal = dimensionResource(R.dimen.padding_md)
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    imageVector: ImageVector? = null,
    contentDescription: String? = null,
    outline: Boolean = false,
    roundedCornerPercentage: Int = 50,
    bgColor: Color? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    fontWeight: FontWeight = FontWeight.Medium,
) {
    val contentColor =
        if (outline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary

    ActionButtonBase(
        onClick = onClick,
        modifier = modifier,
        outline = outline,
        roundedCornerPercentage = roundedCornerPercentage,
        bgColor = bgColor,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                    tint = contentColor
                )
                if (text != null) Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacer_sm)))
            }
            if (text != null) {
                Text(
                    text = text,
                    color = contentColor,
                    style = textStyle,
                    fontWeight = fontWeight
                )
            }
        }
    }
}
