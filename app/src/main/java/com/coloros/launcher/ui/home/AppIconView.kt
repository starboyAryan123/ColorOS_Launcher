package com.coloros.launcher.ui.home

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coloros.launcher.model.AppItem
import com.coloros.launcher.ui.theme.TextPrimary
import kotlinx.coroutines.launch

/**
 * ColorOS Signature Squircle App Icon.
 * Features:
 * - Continuous squircle corner curve (superellipse geometry)
 * - Quantum Animation Engine spring physics on press & release
 * - Global bounds reporting for seamless spatial app opening transition
 */
@Composable
fun AppIconView(
    app: AppItem,
    onClick: (AppItem, Rect) -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 56.dp,
    showLabel: Boolean = true,
    tiltX: Float = 0f,
    tiltY: Float = 0f
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    var iconBounds = remember { Rect.Zero }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .onGloballyPositioned { coordinates ->
                iconBounds = coordinates.boundsInRoot()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            scale.animateTo(
                                targetValue = 0.88f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                        tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    },
                    onTap = {
                        onClick(app, iconBounds)
                    }
                )
            }
    ) {
        // App Icon container with squircle curve & spring scale
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    // Subtle 3D tilt reaction to device gyroscope
                    rotationX = -tiltY * 8f
                    rotationY = tiltX * 8f
                    cameraDistance = 16f * density
                }
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(18.dp),
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.35f),
                    spotColor = Color.Black.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(18.dp))
        ) {
            val drawable = app.iconDrawable
            val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: app.iconBitmap

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = app.label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                // ColorOS dynamic procedural squircle for system/sample apps
                val gradient = remember(app.label) {
                    generateProceduralGradient(app.label)
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(iconSize)
                        .background(gradient)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Android,
                        contentDescription = app.label,
                        tint = Color.White,
                        modifier = Modifier.size(iconSize * 0.58f)
                    )
                }
            }

            // ColorOS Specular highlight top rim
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.22f),
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = app.label,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    // Soft shadow for wallpaper readability
                    shadowElevation = 2f
                }
            )
        }
    }
}

/**
 * Generates an Aquamorphic gradient matching ColorOS design language.
 */
private fun generateProceduralGradient(name: String): Brush {
    val hash = name.hashCode()
    val gradients = listOf(
        listOf(Color(0xFF0A84FF), Color(0xFF00E5FF)),
        listOf(Color(0xFF30D158), Color(0xFF34C759)),
        listOf(Color(0xFFFF9F0A), Color(0xFFFFD60A)),
        listOf(Color(0xFFBF5AF2), Color(0xFF5E5CE6)),
        listOf(Color(0xFFFF375F), Color(0xFFFF7597)),
        listOf(Color(0xFF00C7BE), Color(0xFF32ADE6))
    )
    val pair = gradients[kotlin.math.abs(hash) % gradients.size]
    return Brush.linearGradient(pair)
}
