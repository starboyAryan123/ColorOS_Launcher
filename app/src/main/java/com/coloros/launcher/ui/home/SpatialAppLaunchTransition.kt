package com.coloros.launcher.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.coloros.launcher.model.AppItem
import kotlin.math.roundToInt

/**
 * ColorOS Quantum Animation Engine: Spatial App Opening Transition.
 * Seamlessly expands an app surface from the exact icon bounds to full screen.
 */
@Composable
fun SpatialAppLaunchTransition(
    app: AppItem,
    originRect: Rect,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(app) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 320,
                easing = FastOutSlowInEasing
            )
        )
        onAnimationEnd()
    }

    val currentProgress = progress.value

    // Interpolate coordinates from icon rect to full screen
    val currentLeft = originRect.left + (0f - originRect.left) * currentProgress
    val currentTop = originRect.top + (0f - originRect.top) * currentProgress
    val currentWidth = originRect.width + (screenWidthPx - originRect.width) * currentProgress
    val currentHeight = originRect.height + (screenHeightPx - originRect.height) * currentProgress
    val currentCornerRadius = 18f + (36f - 18f) * currentProgress

    val currentWidthDp = with(density) { currentWidth.toDp() }
    val currentHeightDp = with(density) { currentHeight.toDp() }
    val cornerRadiusDp = with(density) { currentCornerRadius.toDp() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f * currentProgress))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset { IntOffset(currentLeft.roundToInt(), currentTop.roundToInt()) }
                .size(currentWidthDp, currentHeightDp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(cornerRadiusDp))
                .clip(RoundedCornerShape(cornerRadiusDp))
                .background(Color(0xFF161F30))
        ) {
            val bitmap = app.iconDrawable?.toBitmap(96, 96) ?: app.iconBitmap
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Android,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}
