package com.hisuperaman.wallety.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.data.model.ThemeMode
import com.hisuperaman.wallety.ui.components.AppScaffold
import com.hisuperaman.wallety.ui.components.ToastHost
import com.hisuperaman.wallety.ui.components.ToastManager
import com.hisuperaman.wallety.ui.screens.analytics.AnalyticsScreen
import com.hisuperaman.wallety.ui.screens.home.HomeScreen
import com.hisuperaman.wallety.ui.screens.settings.BackupRestoreScreen
import com.hisuperaman.wallety.ui.screens.settings.SettingsScreen
import com.hisuperaman.wallety.ui.screens.transactions.TransactionsScreen
import com.hisuperaman.wallety.ui.viewmodel.AccountViewModel
import com.hisuperaman.wallety.ui.viewmodel.BackupScheduleViewModel
import com.hisuperaman.wallety.ui.viewmodel.DriveViewModel
import com.hisuperaman.wallety.ui.viewmodel.TransactionViewModel


enum class NavRoutes(@StringRes val title: Int) {
    Home(title = R.string.home),
    Transactions(title = R.string.transactions),
    Analytics(title = R.string.analytics),
    Settings(title = R.string.settings),
    BackupRestore(title = R.string.backup_restore),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val previousRoute = navController.previousBackStackEntry?.destination?.route

    val currentScreenTitle = stringResource(
        id = NavRoutes.valueOf(
            currentRoute ?: NavRoutes.Home.name
        ).title
    )


    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = "main_graph",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            navigation(
                startDestination = NavRoutes.Home.name,
                route = "main_graph"
            ) {
                composable(route = NavRoutes.Home.name) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("main_graph")
                    }
                    val accountViewModel = hiltViewModel<AccountViewModel>(parentEntry)
                    val transactionViewModel = hiltViewModel<TransactionViewModel>(parentEntry)
                    val driveViewModel = hiltViewModel<DriveViewModel>(parentEntry)
                    val context = LocalContext.current

                    LaunchedEffect(Unit) {
                        driveViewModel.checkLogin(context)
                    }

                    AppScaffold(
                        title = stringResource(R.string.app_name),
                        currentRoute = currentRoute,
                        navController = navController,
                        isScrollable = true
                    ) {
                        HomeScreen(
                            accountViewModel = accountViewModel,
                            transactionViewModel = transactionViewModel,
                            onSeeMoreClick = {
                                if (currentRoute != NavRoutes.Transactions.name) {
                                    navController.navigate(NavRoutes.Transactions.name) {
                                        launchSingleTop = true
                                        popUpTo(NavRoutes.Transactions.name) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }
                composable(route = NavRoutes.Transactions.name) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("main_graph")
                    }
                    val transactionViewModel = hiltViewModel<TransactionViewModel>(parentEntry)

                    AppScaffold(
                        title = stringResource(R.string.app_name),
                        currentRoute = currentRoute,
                        navController = navController,
                    ) {
                        TransactionsScreen(
                            transactionViewModel = transactionViewModel
                        )
                    }
                }
                composable(route = NavRoutes.Analytics.name) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("main_graph")
                    }
                    val transactionViewModel = hiltViewModel<TransactionViewModel>(parentEntry)

                    AppScaffold(
                        title = stringResource(R.string.app_name),
                        currentRoute = currentRoute,
                        navController = navController,
                    ) {
                        AnalyticsScreen(
                            transactionViewModel = transactionViewModel
                        )
                    }
                }

                navigation(
                    startDestination = NavRoutes.Settings.name,
                    route = "settings_graph",
                ) {
                    composable(
                        route = NavRoutes.Settings.name,
                        enterTransition = { slideInHorizontally { it } },
                        exitTransition = { slideOutHorizontally { it } }
                    ) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main_graph")
                        }
                        val accountViewModel = hiltViewModel<AccountViewModel>(parentEntry)

                        AppScaffold(
                            title = currentScreenTitle,
                            currentRoute = currentRoute,
                            navController = navController,
                            showBottomBar = false,
                            canNavigate = true
                        ) {
                            SettingsScreen(
                                themeMode = themeMode,
                                onThemeChange = onThemeChange,
                                accountViewModel = accountViewModel,
                                onBackupRestoreClick = {
                                    if (currentRoute != NavRoutes.BackupRestore.name) {
                                        navController.navigate(NavRoutes.BackupRestore.name) {
                                            launchSingleTop = true
                                            popUpTo(NavRoutes.BackupRestore.name) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                    }
                    composable(
                        route = NavRoutes.BackupRestore.name,
                        enterTransition = { slideInHorizontally { it } },
                        exitTransition = { slideOutHorizontally { it } }
                    ) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main_graph")
                        }
                        val driveViewModel = hiltViewModel<DriveViewModel>(parentEntry)
                        val backupScheduleViewModel = hiltViewModel<BackupScheduleViewModel>(parentEntry)

                        AppScaffold(
                            title = currentScreenTitle,
                            currentRoute = currentRoute,
                            navController = navController,
                            showBottomBar = false,
                            canNavigate = true
                        ) {
                            BackupRestoreScreen(
                                driveViewModel = driveViewModel,
                                backupScheduleViewModel = backupScheduleViewModel
                            )
                        }
                    }
                }
            }
        }

        ToastHost(
            messages = ToastManager.messages
        )
    }
}
