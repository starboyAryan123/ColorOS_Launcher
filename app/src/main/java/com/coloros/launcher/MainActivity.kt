package com.coloros.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.coloros.launcher.data.AppRepository
import com.coloros.launcher.data.LauncherPreferences
import com.coloros.launcher.model.AppItem
import com.coloros.launcher.sensor.GyroscopeParallaxSensor
import com.coloros.launcher.ui.home.HomeScreen
import com.coloros.launcher.ui.theme.AquaBlue
import com.coloros.launcher.ui.theme.ColorOSLauncherTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var appRepository: AppRepository
    private lateinit var preferences: LauncherPreferences
    private lateinit var gyroscopeSensor: GyroscopeParallaxSensor

    private val packageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Refresh apps list when an app is installed, uninstalled, or updated
            reloadApps()
        }
    }

    private var appsListState = mutableStateOf<List<AppItem>>(emptyList())
    private var isLoadingState = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appRepository = AppRepository(this)
        preferences = LauncherPreferences(this)
        gyroscopeSensor = GyroscopeParallaxSensor(this)

        registerPackageReceiver()
        reloadApps()

        setContent {
            ColorOSLauncherTheme {
                val apps by remember { appsListState }
                val isLoading by remember { isLoadingState }

                if (isLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(color = AquaBlue)
                    }
                } else {
                    HomeScreen(
                        apps = apps,
                        appRepository = appRepository,
                        preferences = preferences,
                        sensor = gyroscopeSensor
                    )
                }
            }
        }
    }

    private fun reloadApps() {
        lifecycleScope.launch {
            val list = appRepository.getInstalledApps()
            appsListState.value = list
            isLoadingState.value = false
        }
    }

    private fun registerPackageReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        registerReceiver(packageChangeReceiver, filter)
    }

    override fun onResume() {
        super.onResume()
        gyroscopeSensor.startListening()
    }

    override fun onPause() {
        super.onPause()
        gyroscopeSensor.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(packageChangeReceiver)
        } catch (e: Exception) {
            // Ignored if not registered
        }
    }
}
