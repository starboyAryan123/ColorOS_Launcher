package com.coloros.launcher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.coloros.launcher.model.AppItem
import com.coloros.launcher.ui.theme.DockGlassBackground
import com.coloros.launcher.ui.theme.GlassBorderSubtle

/**
 * ColorOS Floating Frosted Glass Dock.
 * Accommodates favorite pinned apps on a floating acrylic island.
 */
@Composable
fun ColorOSDock(
    dockApps: List<AppItem>,
    onAppClick: (AppItem, Rect) -> Unit,
    tiltX: Float = 0f,
    tiltY: Float = 0f,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Floating Acrylic Frosted Glass Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
                .graphicsLayer {
                    // Parallax tilt on dock
                    rotationX = -tiltY * 3f
                    rotationY = tiltX * 3f
                }
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.35f),
                    spotColor = Color.Black.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(DockGlassBackground)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f)
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
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dockApps.take(5).forEach { app ->
                    AppIconView(
                        app = app,
                        onClick = onAppClick,
                        showLabel = false,
                        iconSize = 52.dp,
                        tiltX = tiltX,
                        tiltY = tiltY
                    )
                }
            }
        }
    }
}
