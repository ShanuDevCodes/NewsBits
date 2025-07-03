package com.shanudevcodes.newsbits.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.shanudevcodes.newsbits.ui.theme.ThemeOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    companion object {
        val THEME_KEY = stringPreferencesKey("theme_option")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color_scheme")
        val FirstLaunchKey = booleanPreferencesKey("first_launch")
    }

    val firstLaunch:Flow<Boolean> = context.dataStore.data.map { preferences->
        preferences[FirstLaunchKey] ?: true
    }
    suspend fun setFirstLaunch(firstLaunch:Boolean){
        context.dataStore.edit { preferences->
            preferences[FirstLaunchKey] = firstLaunch
        }
    }

    val themeFlow: Flow<ThemeOptions> = context.dataStore.data.map { preferences ->
        ThemeOptions.valueOf(preferences[THEME_KEY] ?: ThemeOptions.SYSTEM_DEFAULT.name)
    }

    suspend fun saveThemeOption(theme: ThemeOptions) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    val dynamicColorFlow: Flow<Boolean> = context.dataStore.data.map{ preferences->
        preferences[DYNAMIC_COLOR_KEY] ?: false
    }

    suspend fun setDynamicColor(dynamicColor: Boolean){
        context.dataStore.edit { preferences->
            preferences[DYNAMIC_COLOR_KEY] = dynamicColor
        }
    }
}