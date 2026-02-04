package com.hisuperaman.wallety.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisuperaman.wallety.data.UserPreferences
import com.hisuperaman.wallety.data.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@HiltViewModel
class ThemeViewModel @Inject constructor() : ViewModel() {
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    fun loadTheme(context: Context) {
        viewModelScope.launch {
            UserPreferences.getThemeMode(context).collect {
                _themeMode.value = it ?: ThemeMode.SYSTEM
            }
        }
    }

    fun setTheme(context: Context, themeMode: ThemeMode) {
        _themeMode.value = themeMode
        viewModelScope.launch {
            UserPreferences.saveSession(context, themeMode)
        }
    }
}
