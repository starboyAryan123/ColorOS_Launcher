package com.coloros.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.coloros.launcher.data.LauncherPreferences
import com.coloros.launcher.ui.settings.LauncherSettingsScreen
import com.coloros.launcher.ui.theme.ColorOSLauncherTheme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = LauncherPreferences(this)

        setContent {
            ColorOSLauncherTheme {
                LauncherSettingsScreen(
                    preferences = preferences,
                    onBack = { finish() }
                )
            }
        }
    }
}
