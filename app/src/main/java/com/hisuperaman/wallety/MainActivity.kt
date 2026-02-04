package com.hisuperaman.wallety

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.hisuperaman.wallety.data.database.AccountRepository
import com.hisuperaman.wallety.data.model.Account
import com.hisuperaman.wallety.data.model.ThemeMode
import com.hisuperaman.wallety.ui.navigation.AppNavHost
import com.hisuperaman.wallety.ui.theme.WalletyTheme
import com.hisuperaman.wallety.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class WalletyApp : Application() {
    @Inject lateinit var repository: AccountRepository

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            // Insert default account if table is empty
            if (repository.account.firstOrNull() == null) {
                repository.upsertAccount(Account(id = 1, balance = 0L, createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { isLoading }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val themeViewModel = hiltViewModel<ThemeViewModel>()
            val context = LocalContext.current
            val themeMode by themeViewModel.themeMode.collectAsState()

            LaunchedEffect(Unit) {
                themeViewModel.loadTheme(context)
            }

            val isDark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            WalletyTheme(darkTheme = isDark) {
                AppNavHost(
                    navController = navController,
                    themeMode = themeMode,
                    onThemeChange = {themeViewModel.setTheme(context, it)}
                )
            }
        }

        lifecycleScope.launch {
            isLoading = false
        }
    }
}
