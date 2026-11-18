package com.coloros.launcher.model

import java.util.UUID

/**
 * Represents a Folder on the ColorOS Home screen.
 * Supports both classic 1x1 folders and ColorOS signature Large Folders (2x2)
 * which allow launching apps directly from the 9-dot preview grid.
 */
data class FolderItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isLargeFolder: Boolean = true,
    val apps: List<AppItem> = emptyList(),
    val pageIndex: Int = 0,
    val gridX: Int = 0,
    val gridY: Int = 0
)
