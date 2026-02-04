package com.hisuperaman.wallety.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hisuperaman.wallety.ui.navigation.NavRoutes
import com.hisuperaman.wallety.R

@Composable
fun AppScaffold(
    title: String,
    showBottomBar: Boolean = true,
    currentRoute: String?,
    navController: NavHostController,
    isScrollable: Boolean = false,
    canNavigate: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                canNavigate = canNavigate,
                onBack = {
                    navController.popBackStack()
                },
                title = title,
                onSettingsClick = {
                    if (currentRoute != NavRoutes.Settings.name) {
                        navController.navigate(NavRoutes.Settings.name) {
                            launchSingleTop = true
                            popUpTo(NavRoutes.Settings.name) { inclusive = true }
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (isScrollable) Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = if (showBottomBar) 100.dp else 0.dp)
                        else Modifier
                    )
            ) {
                content()
            }
            if (showBottomBar) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { destination ->
                        if (currentRoute != destination) {
                            navController.navigate(destination) {
                                launchSingleTop = true
                                popUpTo(destination) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}