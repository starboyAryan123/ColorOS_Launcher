package com.coloros.launcher.model

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

/**
 * Represents an installed application item in ColorOS Launcher.
 */
data class AppItem(
    val id: String,
    val packageName: String,
    val activityName: String,
    val label: String,
    val iconDrawable: Drawable? = null,
    val iconBitmap: Bitmap? = null,
    val category: String = "General",
    val installTime: Long = 0L,
    val isSystemApp: Boolean = false
) {
    val firstLetter: Char
        get() = label.firstOrNull()?.uppercaseChar() ?: '#'
}
