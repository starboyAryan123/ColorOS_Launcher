package com.coloros.launcher.ui.home

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.coloros.launcher.SettingsActivity
import com.coloros.launcher.data.AppRepository
import com.coloros.launcher.data.LauncherPreferences
import com.coloros.launcher.model.AppItem
import com.coloros.launcher.model.FolderItem
import com.coloros.launcher.model.TransitionEffect
import com.coloros.launcher.sensor.GyroscopeParallaxSensor
import com.coloros.launcher.ui.drawer.AppDrawerSheet
import com.coloros.launcher.ui.folder.FolderExpandedDialog
import com.coloros.launcher.ui.folder.LargeFolderView
import com.coloros.launcher.ui.theme.AquaCyan
import com.coloros.launcher.ui.widgets.AquamorphicClockWidget
import com.coloros.launcher.ui.widgets.QuickSearchPill
import com.coloros.launcher.ui.widgets.RamCleanerWidget
import kotlinx.coroutines.launch

/**
 * ColorOS Core Home Screen Component.
 * Integrates:
 * - Multi-layer Gyroscope Parallax Wallpaper
 * - Quantum Animation Engine (Cube, Cylinder, Flip, Card, Wave page transitions)
 * - ColorOS 2x2 Large Folders
 * - Spatial App Launch Expansion
 * - Acrylic Frosted Glass Dock and App Drawer
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    apps: List<AppItem>,
    appRepository: AppRepository,
    preferences: LauncherPreferences,
    sensor: GyroscopeParallaxSensor,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val transitionEffect by preferences.transitionEffectFlow.collectAsState(initial = TransitionEffect.CUBE)
    val depthParallaxEnabled by preferences.enableDepthParallaxFlow.collectAsState(initial = true)
    val gyroSensitivity by preferences.gyroscopeSensitivityFlow.collectAsState(initial = 1.0f)
    val largeFoldersEnabled by preferences.enableLargeFoldersFlow.collectAsState(initial = true)

    val tiltX = if (depthParallaxEnabled) sensor.tiltX.value * gyroSensitivity else 0f
    val tiltY = if (depthParallaxEnabled) sensor.tiltY.value * gyroSensitivity else 0f

    val pagerState = rememberPagerState(pageCount = { 2 })

    // Active dialog states
    var isDrawerOpen by remember { mutableStateOf(false) }
    var activeFolder by remember { mutableStateOf<FolderItem?>(null) }

    // Spatial app launch animation state
    var launchingApp by remember { mutableStateOf<AppItem?>(null) }
    var launchingRect by remember { mutableStateOf(Rect.Zero) }

    // Handle system back press
    BackHandler(enabled = isDrawerOpen || activeFolder != null || pagerState.currentPage != 0) {
        when {
            launchingApp != null -> launchingApp = null
            activeFolder != null -> activeFolder = null
            isDrawerOpen -> isDrawerOpen = false
            pagerState.currentPage != 0 -> {
                scope.launch { pagerState.animateScrollToPage(0) }
            }
        }
    }

    // Split apps between Dock, Page 1, Page 2, and Folders
    val dockApps = remember(apps) {
        apps.filter {
            val name = (it.packageName + it.label).lowercase()
            name.contains("dialer") || name.contains("phone") ||
                    name.contains("message") || name.contains("mms") ||
                    name.contains("chrome") || name.contains("browser") ||
                    name.contains("camera") || name.contains("setting")
        }.take(4).ifEmpty { apps.take(4) }
    }

    val nonDockApps = remember(apps, dockApps) {
        apps.filterNot { dockApps.contains(it) }
    }

    // Create sample ColorOS Large Folder (Tools & Media)
    val sampleFolder = remember(nonDockApps) {
        FolderItem(
            title = "ColorOS Tools",
            isLargeFolder = true,
            apps = nonDockApps.take(9)
        )
    }

    val page1Apps = remember(nonDockApps) { nonDockApps.drop(9).take(8) }
    val page2Apps = remember(nonDockApps) { nonDockApps.drop(17).take(16) }

    val currentOffsetFraction = pagerState.currentPage + pagerState.currentPageOffsetFraction
    val blurRadius = if (isDrawerOpen || activeFolder != null) 22f else 0f

    Box(modifier = modifier.fillMaxSize()) {
        // Multi-Layer Parallax Wallpaper
        ParallaxWallpaperView(
            scrollProgress = currentOffsetFraction,
            tiltX = tiltX,
            tiltY = tiltY,
            blurRadius = blurRadius
        )

        // Main Home Screen Drag & Swipe Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .draggable(
                    state = rememberDraggableState { deltaY ->
                        // Swipe up gesture triggers App Drawer
                        if (deltaY < -24f && !isDrawerOpen) {
                            isDrawerOpen = true
                        }
                    },
                    orientation = Orientation.Vertical
                )
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                    onLongClick = {
                        // Long press anywhere opens ColorOS Launcher Settings & Transitions picker
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Horizontal Pager with ColorOS Quantum Animation Engine Transitions
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { pageIndex ->
                    val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .colorOsPageTransition(pageOffset, transitionEffect)
                    ) {
                        if (pageIndex == 0) {
                            // Page 0: Widgets + Large Folder + Quick Apps
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp)
                            ) {
                                AquamorphicClockWidget(tiltX = tiltX, tiltY = tiltY)
                                QuickSearchPill(
                                    onSearchClick = { isDrawerOpen = true },
                                    tiltX = tiltX,
                                    tiltY = tiltY
                                )
                                RamCleanerWidget(tiltX = tiltX, tiltY = tiltY)

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    if (largeFoldersEnabled) {
                                        LargeFolderView(
                                            folder = sampleFolder,
                                            onAppClick = { app, rect ->
                                                launchingApp = app
                                                launchingRect = rect
                                            },
                                            onExpandFolder = { activeFolder = it },
                                            tiltX = tiltX,
                                            tiltY = tiltY
                                        )
                                    }

                                    // Quick grid alongside folder
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        page1Apps.take(4).chunked(2).forEach { rowApps ->
                                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                rowApps.forEach { app ->
                                                    AppIconView(
                                                        app = app,
                                                        onClick = { clickedApp, rect ->
                                                            launchingApp = clickedApp
                                                            launchingRect = rect
                                                        },
                                                        tiltX = tiltX,
                                                        tiltY = tiltY,
                                                        iconSize = 54.dp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Page 1: Secondary App Grid
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 24.dp)
                            ) {
                                page2Apps.chunked(4).forEach { rowApps ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        rowApps.forEach { app ->
                                            AppIconView(
                                                app = app,
                                                onClick = { clickedApp, rect ->
                                                    launchingApp = clickedApp
                                                    launchingRect = rect
                                                },
                                                tiltX = tiltX,
                                                tiltY = tiltY,
                                                iconSize = 54.dp
                                            )
                                        }
                                        repeat(4 - rowApps.size) {
                                            Spacer(modifier = Modifier.size(54.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ColorOS Aquamorphic Page Indicator Dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(2) { index ->
                        val isCurrent = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (isCurrent) 16.dp else 6.dp, 6.dp)
                                .clip(if (isCurrent) RoundedCornerShape(3.dp) else CircleShape)
                                .background(if (isCurrent) AquaCyan else Color.White.copy(alpha = 0.4f))
                        )
                    }
                }

                // Frosted Glass Floating Dock
                ColorOSDock(
                    dockApps = dockApps,
                    onAppClick = { clickedApp, rect ->
                        launchingApp = clickedApp
                        launchingRect = rect
                    },
                    tiltX = tiltX,
                    tiltY = tiltY
                )
            }
        }

        // Expanded Large Folder Modal
        FolderExpandedDialog(
            folder = activeFolder,
            onDismiss = { activeFolder = null },
            onAppClick = { clickedApp, rect ->
                launchingApp = clickedApp
                launchingRect = rect
            }
        )

        // App Drawer Sheet
        AppDrawerSheet(
            isOpen = isDrawerOpen,
            apps = apps,
            onClose = { isDrawerOpen = false },
            onAppClick = { clickedApp, rect ->
                launchingApp = clickedApp
                launchingRect = rect
            }
        )

        // Quantum Animation Engine: Spatial Launch Expansion
        val currentLaunchingApp = launchingApp
        if (currentLaunchingApp != null) {
            SpatialAppLaunchTransition(
                app = currentLaunchingApp,
                originRect = launchingRect,
                onAnimationEnd = {
                    val appToLaunch = currentLaunchingApp
                    launchingApp = null
                    appRepository.launchApp(appToLaunch)
                }
            )
        }
    }
}
