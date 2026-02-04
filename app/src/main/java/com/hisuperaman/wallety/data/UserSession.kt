package com.hisuperaman.wallety.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hisuperaman.wallety.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object UserPreferences {
    private val Context.dataStore by preferencesDataStore("user_prefs")

    private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")

    suspend fun saveSession(context: Context, themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = themeMode.name
        }
    }

    suspend fun clearSession(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_THEME_MODE)
        }
    }

    fun getThemeMode(context: Context): Flow<ThemeMode?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_THEME_MODE]?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM
        }
    }
}