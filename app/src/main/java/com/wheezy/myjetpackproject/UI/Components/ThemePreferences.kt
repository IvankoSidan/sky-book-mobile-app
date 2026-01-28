package com.wheezy.myjetpackproject.UI.Components

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wheezy.myjetpackproject.Data.Enums.ThemeOption
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore by preferencesDataStore(name = "theme_prefs")
@Singleton
class ThemePreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore
    private val THEME_KEY = stringPreferencesKey("current_theme")

    val currentTheme: Flow<ThemeOption> = dataStore.data
        .map { prefs ->
            val themeName = prefs[THEME_KEY] ?: ThemeOption.Auto.name
            ThemeOption.valueOf(themeName)
        }

    suspend fun setTheme(theme: ThemeOption) {
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }
}