package com.hisuperaman.wallety.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hisuperaman.wallety.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    canNavigate: Boolean = false,
    title: String?,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (canNavigate && title!=null) title else stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        navigationIcon = {
            if (canNavigate) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBackIos,
                        contentDescription = "back"
                    )
                }
            }
        },
        actions = {
            if (!canNavigate) {
                ActionButton(
                    imageVector = Icons.Default.Settings,
                    outline = true,
                    onClick = onSettingsClick
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}