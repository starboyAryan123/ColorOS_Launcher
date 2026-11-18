package com.coloros.launcher.ui.folder

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coloros.launcher.model.AppItem
import com.coloros.launcher.model.FolderItem
import com.coloros.launcher.ui.home.AppIconView
import com.coloros.launcher.ui.theme.FolderGlassBackground
import com.coloros.launcher.ui.theme.GlassBorderSubtle
import com.coloros.launcher.ui.theme.TextPrimary
import kotlinx.coroutines.launch

/**
 * ColorOS Signature Large Folder (2x2 Grid).
 * Key Feature: Users can tap apps directly inside the 9-dot preview to launch them,
 * or tap the folder card/header to expand into full folder view.
 */
@Composable
fun LargeFolderView(
    folder: FolderItem,
    onAppClick: (AppItem, Rect) -> Unit,
    onExpandFolder: (FolderItem) -> Unit,
    tiltX: Float = 0f,
    tiltY: Float = 0f,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(8.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                rotationX = -tiltY * 4f
                rotationY = tiltX * 4f
            }
    ) {
        // Frosted Glass Large Folder Container (2x2 footprint)
        Box(
            modifier = Modifier
                .size(164.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(FolderGlassBackground)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.15f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.35f),
                            GlassBorderSubtle
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            scope.launch {
                                scale.animateTo(0.96f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            }
                            tryAwaitRelease()
                            scope.launch {
                                scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            }
                        },
                        onTap = {
                            onExpandFolder(folder)
                        }
                    )
                }
                .padding(10.dp)
        ) {
            // 3x3 Mini Grid of Apps inside the Large Folder
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val apps = folder.apps.take(9)
                val rows = apps.chunked(3)

                rows.forEach { rowApps ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowApps.forEach { app ->
                            AppIconView(
                                app = app,
                                onClick = onAppClick,
                                iconSize = 36.dp,
                                showLabel = false,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                        // Fill empty cells if row has fewer than 3
                        repeat(3 - rowApps.size) {
                            Spacer(modifier = Modifier.size(44.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = folder.title,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { onExpandFolder(folder) }
        )
    }
}
