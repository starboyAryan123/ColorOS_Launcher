package com.coloros.launcher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.coloros.launcher.model.TransitionEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.launcherDataStore: DataStore<Preferences> by preferencesDataStore(name = "coloros_launcher_prefs")

class LauncherPreferences(private val context: Context) {

    private val dataStore = context.launcherDataStore

    companion object {
        val KEY_TRANSITION_EFFECT = stringPreferencesKey("transition_effect")
        val KEY_ENABLE_DEPTH_PARALLAX = booleanPreferencesKey("enable_depth_parallax")
        val KEY_GYROSCOPE_SENSITIVITY = floatPreferencesKey("gyroscope_sensitivity")
        val KEY_ENABLE_LARGE_FOLDERS = booleanPreferencesKey("enable_large_folders")
        val KEY_GRID_ROWS = intPreferencesKey("grid_rows")
        val KEY_GRID_COLS = intPreferencesKey("grid_cols")
        val KEY_ICON_CORNER_RADIUS = floatPreferencesKey("icon_corner_radius")
    }

    val transitionEffectFlow: Flow<TransitionEffect> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val name = preferences[KEY_TRANSITION_EFFECT] ?: TransitionEffect.CUBE.name
            try {
                TransitionEffect.valueOf(name)
            } catch (e: Exception) {
                TransitionEffect.CUBE
            }
        }

    val enableDepthParallaxFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_ENABLE_DEPTH_PARALLAX] ?: true
        }

    val gyroscopeSensitivityFlow: Flow<Float> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_GYROSCOPE_SENSITIVITY] ?: 1.0f
        }

    val enableLargeFoldersFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_ENABLE_LARGE_FOLDERS] ?: true
        }

    val gridRowsFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_GRID_ROWS] ?: 5
        }

    val gridColsFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_GRID_COLS] ?: 4
        }

    suspend fun setTransitionEffect(effect: TransitionEffect) {
        dataStore.edit { preferences ->
            preferences[KEY_TRANSITION_EFFECT] = effect.name
        }
    }

    suspend fun setDepthParallaxEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_ENABLE_DEPTH_PARALLAX] = enabled
        }
    }

    suspend fun setGyroscopeSensitivity(sensitivity: Float) {
        dataStore.edit { preferences ->
            preferences[KEY_GYROSCOPE_SENSITIVITY] = sensitivity
        }
    }

    suspend fun setLargeFoldersEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_ENABLE_LARGE_FOLDERS] = enabled
        }
    }

    suspend fun setGridLayout(rows: Int, cols: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_GRID_ROWS] = rows
            preferences[KEY_GRID_COLS] = cols
        }
    }
}
