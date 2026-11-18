package com.coloros.launcher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.widget.Toast
import com.coloros.launcher.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    /**
     * Query all installed launcher applications asynchronously.
     */
    suspend fun getInstalledApps(): List<AppItem> = withContext(Dispatchers.IO) {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = try {
            packageManager.queryIntentActivities(launcherIntent, 0)
        } catch (e: Exception) {
            emptyList()
        }

        val selfPackage = context.packageName

        val apps = resolveInfos
            .filter { it.activityInfo.packageName != selfPackage }
            .map { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                val activityName = resolveInfo.activityInfo.name
                val label = resolveInfo.loadLabel(packageManager).toString()
                val icon = try {
                    resolveInfo.loadIcon(packageManager)
                } catch (e: Exception) {
                    null
                }

                AppItem(
                    id = "$packageName/$activityName",
                    packageName = packageName,
                    activityName = activityName,
                    label = label,
                    iconDrawable = icon,
                    category = categorizeApp(packageName, label)
                )
            }
            .sortedBy { it.label.lowercase() }

        if (apps.isEmpty()) {
            getFallbackSampleApps()
        } else {
            apps
        }
    }

    /**
     * Launch target application with ColorOS-compatible intent flags.
     */
    fun launchApp(appItem: AppItem): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(appItem.packageName, appItem.activityName)
                addCategory(Intent.CATEGORY_LAUNCHER)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val launchIntent = packageManager.getLaunchIntentForPackage(appItem.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    true
                } else {
                    Toast.makeText(context, "Cannot launch ${appItem.label}", Toast.LENGTH_SHORT).show()
                    false
                }
            } catch (ex: Exception) {
                Toast.makeText(context, "Error: ${ex.localizedMessage}", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    private fun categorizeApp(packageName: String, label: String): String {
        val lower = (packageName + label).lowercase()
        return when {
            lower.contains("camera") || lower.contains("photo") || lower.contains("gallery") -> "Photography"
            lower.contains("music") || lower.contains("audio") || lower.contains("sound") || lower.contains("spotify") -> "Media"
            lower.contains("message") || lower.contains("chat") || lower.contains("mail") || lower.contains("whatsapp") -> "Communication"
            lower.contains("game") -> "Games"
            lower.contains("setting") || lower.contains("system") || lower.contains("file") -> "Tools"
            else -> "General"
        }
    }

    /**
     * Pre-populated ColorOS-styled apps for preview or fresh installations.
     */
    fun getFallbackSampleApps(): List<AppItem> {
        return listOf(
            AppItem("com.android.dialer", "com.android.dialer", "DialerActivity", "Phone", null, null, "Communication"),
            AppItem("com.android.mms", "com.android.mms", "MmsActivity", "Messages", null, null, "Communication"),
            AppItem("com.android.chrome", "com.android.chrome", "Main", "Chrome", null, null, "General"),
            AppItem("com.oppo.camera", "com.oppo.camera", "Camera", "Camera", null, null, "Photography"),
            AppItem("com.coloros.gallery", "com.coloros.gallery", "Gallery", "Photos", null, null, "Photography"),
            AppItem("com.android.settings", "com.android.settings", "Settings", "Settings", null, null, "Tools"),
            AppItem("com.coloros.filemanager", "com.coloros.filemanager", "FileManager", "Files", null, null, "Tools"),
            AppItem("com.oppo.music", "com.oppo.music", "MusicActivity", "Music", null, null, "Media"),
            AppItem("com.coloros.weather", "com.coloros.weather", "WeatherActivity", "Weather", null, null, "Tools"),
            AppItem("com.coloros.clock", "com.coloros.clock", "ClockActivity", "Clock", null, null, "Tools"),
            AppItem("com.coloros.note", "com.coloros.note", "NoteActivity", "Notes", null, null, "Tools"),
            AppItem("com.coloros.calculator", "com.coloros.calculator", "CalculatorActivity", "Calculator", null, null, "Tools"),
            AppItem("com.coloros.compass", "com.coloros.compass", "CompassActivity", "Compass", null, null, "Tools"),
            AppItem("com.heytap.market", "com.heytap.market", "MarketActivity", "App Market", null, null, "General"),
            AppItem("com.heytap.themestore", "com.heytap.themestore", "ThemeStoreActivity", "Theme Store", null, null, "General"),
            AppItem("com.google.android.youtube", "com.google.android.youtube", "YouTubeActivity", "YouTube", null, null, "Media"),
            AppItem("com.google.android.gm", "com.google.android.gm", "GmailActivity", "Gmail", null, null, "Communication"),
            AppItem("com.google.android.apps.maps", "com.google.android.apps.maps", "MapsActivity", "Maps", null, null, "General"),
            AppItem("com.coloros.gamespace", "com.coloros.gamespace", "GameSpaceActivity", "Games", null, null, "Games"),
            AppItem("com.coloros.phonemanager", "com.coloros.phonemanager", "PhoneManagerActivity", "Phone Manager", null, null, "Tools")
        )
    }
}
