package com.coloros.launcher.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coloros.launcher.data.LauncherPreferences
import com.coloros.launcher.model.TransitionEffect
import com.coloros.launcher.ui.theme.AquaBlue
import com.coloros.launcher.ui.theme.AquaCyan
import com.coloros.launcher.ui.theme.AquaGradientEnd
import com.coloros.launcher.ui.theme.AquaGradientStart
import com.coloros.launcher.ui.theme.GlassBorderSubtle
import com.coloros.launcher.ui.theme.GlassSurfaceLight
import com.coloros.launcher.ui.theme.TextPrimary
import com.coloros.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * ColorOS Launcher Customization & Transition Animation Settings.
 */
@Composable
fun LauncherSettingsScreen(
    preferences: LauncherPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val currentTransition by preferences.transitionEffectFlow.collectAsState(initial = TransitionEffect.CUBE)
    val depthParallaxEnabled by preferences.enableDepthParallaxFlow.collectAsState(initial = true)
    val gyroSensitivity by preferences.gyroscopeSensitivityFlow.collectAsState(initial = 1.0f)
    val largeFoldersEnabled by preferences.enableLargeFoldersFlow.collectAsState(initial = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AquaGradientStart, Color(0xFF0F172A), AquaGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Home Screen & Transitions",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Set as Default Home Screen Action Card
            Card(
                colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(AquaCyan.copy(alpha = 0.5f), GlassBorderSubtle)),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Set as Default Launcher",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Experience ColorOS animations on any Android smartphone.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )
                    Button(
                        onClick = { openDefaultHomeSettings(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = AquaBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply as Default Home Screen", color = Color.White)
                    }
                }
            }

            // Page Transition Animations Section
            Text(
                text = "PAGE TRANSITION ANIMATION",
                color = AquaCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    TransitionEffect.values().forEach { effect ->
                        val isSelected = effect == currentTransition
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    scope.launch { preferences.setTransitionEffect(effect) }
                                }
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) AquaCyan else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Column(modifier = Modifier.padding(start = 14.dp)) {
                                Text(
                                    text = effect.title,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                                Text(
                                    text = effect.description,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Depth & Gyroscope Parallax Section
            Text(
                text = "DEPTH EFFECTS & SENSORS",
                color = AquaCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "3D Wallpaper Depth Parallax",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Multi-plane tilt reacting to phone tilt & page gestures",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = depthParallaxEnabled,
                            onCheckedChange = { scope.launch { preferences.setDepthParallaxEnabled(it) } },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AquaBlue
                            )
                        )
                    }

                    if (depthParallaxEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Gyroscope Responsiveness: ${(gyroSensitivity * 100).toInt()}%",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Slider(
                            value = gyroSensitivity,
                            onValueChange = { scope.launch { preferences.setGyroscopeSensitivity(it) } },
                            valueRange = 0.2f..2.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = AquaCyan,
                                activeTrackColor = AquaBlue
                            )
                        )
                    }
                }
            }

            // Folder Style Section
            Text(
                text = "FOLDERS & ICONS",
                color = AquaCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ColorOS Large Folders (2x2)",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Launch apps directly with a single tap inside the folder card",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = largeFoldersEnabled,
                        onCheckedChange = { scope.launch { preferences.setLargeFoldersEnabled(it) } },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AquaBlue
                        )
                    )
                }
            }
        }
    }
}

private fun openDefaultHomeSettings(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            context.startActivity(intent)
        } else {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        context.startActivity(intent)
    }
}
