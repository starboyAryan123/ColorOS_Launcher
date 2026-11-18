package com.coloros.launcher.ui.widgets

import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coloros.launcher.ui.theme.AquaGreen
import com.coloros.launcher.ui.theme.GlassBorderSubtle
import com.coloros.launcher.ui.theme.GlassSurfaceLight
import com.coloros.launcher.ui.theme.TextPrimary
import com.coloros.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * ColorOS Phone Manager / RAM Speed Booster Widget.
 * Clean, interactive memory optimization with fluid water ripple feedback.
 */
@Composable
fun RamCleanerWidget(
    tiltX: Float = 0f,
    tiltY: Float = 0f,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    var ramPercent by remember { mutableIntStateOf(68) }
    var isOptimized by remember { mutableStateOf(false) }
    val rotationAngle = remember { Animatable(0f) }
    val pulseScale = remember { Animatable(1f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .graphicsLayer {
                rotationX = -tiltY * 3f
                rotationY = tiltX * 3f
            }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(GlassSurfaceLight)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.3f), GlassBorderSubtle)
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                scope.launch {
                    pulseScale.animateTo(0.92f, tween(100))
                    rotationAngle.animateTo(rotationAngle.value + 360f, tween(600))
                    pulseScale.animateTo(1f, tween(200))
                    ramPercent = 48
                    isOptimized = true
                    Toast
                        .makeText(context, "System Optimized! 460 MB Released", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer {
                            rotationZ = rotationAngle.value
                            scaleX = pulseScale.value
                            scaleY = pulseScale.value
                        }
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    AquaGreen,
                                    Color(0xFF00E5FF),
                                    Color(0xFF0A84FF),
                                    AquaGreen
                                )
                            )
                        )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF131F35))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = "Optimize",
                            tint = AquaGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = if (isOptimized) "System Optimal" else "Phone Speed Boost",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "RAM Available: ${100 - ramPercent}%",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Tap to Boost",
                    color = AquaGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
