package com.hisuperaman.wallety.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hisuperaman.wallety.ui.navigation.NavRoutes
import com.hisuperaman.wallety.R

@Composable
fun NavItem(
    onClick: () -> Unit,
    imageVector: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    val color = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(50))
            .background(color)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues = PaddingValues(bottom = dimensionResource(R.dimen.padding_xl)))
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(60.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_xs)),
            ) {
                NavItem(
                    imageVector = Icons.Outlined.Home,
                    onClick = {onNavigate(NavRoutes.Home.name)},
                    selected = currentRoute == NavRoutes.Home.name,
                    contentDescription = stringResource(NavRoutes.Home.title),
                    modifier = Modifier.weight(1f)
                )
                NavItem(
                    imageVector = Icons.AutoMirrored.Outlined.List,
                    onClick = {onNavigate(NavRoutes.Transactions.name)},
                    selected = currentRoute == NavRoutes.Transactions.name,
                    contentDescription = stringResource(NavRoutes.Transactions.title),
                    modifier = Modifier.weight(1f)
                )
                NavItem(
                    imageVector = Icons.Default.Analytics,
                    onClick = {onNavigate(NavRoutes.Analytics.name)},
                    selected = currentRoute == NavRoutes.Analytics.name,
                    contentDescription = stringResource(NavRoutes.Analytics.title),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}