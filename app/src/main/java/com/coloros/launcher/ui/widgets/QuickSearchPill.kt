package com.coloros.launcher.ui.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coloros.launcher.ui.theme.AquaCyan
import com.coloros.launcher.ui.theme.GlassBorderSubtle
import com.coloros.launcher.ui.theme.GlassSurfaceLight
import com.coloros.launcher.ui.theme.TextSecondary

/**
 * ColorOS Quick Search Pill Widget.
 * Frosted translucent floating search pill with voice search shortcut.
 */
@Composable
fun QuickSearchPill(
    onSearchClick: () -> Unit,
    tiltX: Float = 0f,
    tiltY: Float = 0f,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
                shape = RoundedCornerShape(26.dp),
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(GlassSurfaceLight)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.14f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.1f)
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
            .clickable(onClick = onSearchClick)
            .height(52.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = AquaCyan,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = "Search or enter address",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = "Voice Search",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_WEB_SEARCH)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            onSearchClick()
                        }
                    }
            )
        }
    }
}
