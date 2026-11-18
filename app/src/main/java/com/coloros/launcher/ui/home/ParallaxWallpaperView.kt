package com.coloros.launcher.ui.home

import android.app.WallpaperManager
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.coloros.launcher.ui.theme.AquaCyan
import com.coloros.launcher.ui.theme.AquaGradientEnd
import com.coloros.launcher.ui.theme.AquaGradientMid
import com.coloros.launcher.ui.theme.AquaGradientStart

/**
 * ColorOS Dynamic Multi-Layer Parallax Wallpaper.
 * Creates physical depth through multi-plane parallax:
 * - Background layer shifts at 0.15x page scroll + low gyro tilt
 * - Midground fluid water orbs shift at 0.40x page scroll + medium gyro tilt
 * - Real-time frosted glass acrylic blur responds to app drawer and folder open state
 */
@Composable
fun ParallaxWallpaperView(
    scrollProgress: Float,
    tiltX: Float,
    tiltY: Float,
    blurRadius: Float = 0f,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val wallpaperManager = remember { WallpaperManager.getInstance(context) }
    val systemWallpaper = remember {
        try {
            val drawable = wallpaperManager.drawable
            (drawable as? BitmapDrawable)?.bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    val animatedBlur by animateFloatAsState(
        targetValue = blurRadius,
        animationSpec = tween(durationMillis = 250),
        label = "blur"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (animatedBlur > 0.5f) Modifier.blur(animatedBlur.dp) else Modifier)
    ) {
        if (systemWallpaper != null) {
            // Parallax System Wallpaper
            Image(
                bitmap = systemWallpaper,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Background moves slower than foreground icons (0.25x)
                        translationX = -scrollProgress * 90f + tiltX * 24f
                        translationY = tiltY * 24f
                        scaleX = 1.18f
                        scaleY = 1.18f
                    }
            )
        } else {
            // ColorOS Signature Aquamorphic Live Canvas Wallpaper
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                AquaGradientStart,
                                AquaGradientMid,
                                AquaGradientEnd
                            )
                        )
                    )
            )

            // Dynamic water ripple depth particles (Midground layer)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = -scrollProgress * 150f + tiltX * 45f
                        translationY = tiltY * 40f
                        scaleX = 1.15f
                        scaleY = 1.15f
                    }
            ) {
                val width = size.width
                val height = size.height

                // Liquid orb 1 - Top right cyan glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AquaCyan.copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(width * 0.82f, height * 0.22f),
                        radius = width * 0.65f
                    ),
                    radius = width * 0.65f,
                    center = Offset(width * 0.82f, height * 0.22f)
                )

                // Liquid orb 2 - Bottom left deep ocean glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0A84FF).copy(alpha = 0.32f), Color.Transparent),
                        center = Offset(width * 0.18f, height * 0.72f),
                        radius = width * 0.75f
                    ),
                    radius = width * 0.75f,
                    center = Offset(width * 0.18f, height * 0.72f)
                )

                // Liquid orb 3 - Center subtle depth ripple
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFBF5AF2).copy(alpha = 0.16f), Color.Transparent),
                        center = Offset(width * 0.5f, height * 0.45f),
                        radius = width * 0.5f
                    ),
                    radius = width * 0.5f,
                    center = Offset(width * 0.5f, height * 0.45f)
                )
            }
        }

        // ColorOS subtle dark vignette gradient for icon legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )
    }
}
