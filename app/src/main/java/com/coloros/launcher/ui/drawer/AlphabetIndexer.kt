package com.coloros.launcher.ui.drawer

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coloros.launcher.ui.theme.AquaCyan
import com.coloros.launcher.ui.theme.TextSecondary

/**
 * ColorOS Vertical Alphabet Quick Indexer (A-Z).
 * Provides quick index jumping with haptic ticks.
 */
@Composable
fun AlphabetIndexer(
    letters: List<Char>,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var totalHeight by remember { mutableStateOf(1) }
    var selectedLetter by remember { mutableStateOf<Char?>(null) }

    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = modifier
            .fillMaxHeight()
            .width(28.dp)
            .padding(vertical = 48.dp)
            .onGloballyPositioned { coordinates ->
                totalHeight = coordinates.size.height
            }
            .pointerInput(letters) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val index = ((offset.y / totalHeight) * letters.size)
                            .toInt()
                            .coerceIn(0, letters.lastIndex)
                        val letter = letters[index]
                        if (letter != selectedLetter) {
                            selectedLetter = letter
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onLetterSelected(letter)
                        }
                    },
                    onVerticalDrag = { change, _ ->
                        val index = ((change.position.y / totalHeight) * letters.size)
                            .toInt()
                            .coerceIn(0, letters.lastIndex)
                        val letter = letters[index]
                        if (letter != selectedLetter) {
                            selectedLetter = letter
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            onLetterSelected(letter)
                        }
                    },
                    onDragEnd = {
                        selectedLetter = null
                    }
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            letters.forEach { char ->
                val isSelected = char == selectedLetter
                Text(
                    text = char.toString(),
                    color = if (isSelected) AquaCyan else TextSecondary,
                    fontSize = if (isSelected) 12.sp else 9.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
