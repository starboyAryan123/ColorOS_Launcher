package com.coloros.launcher.ui.home

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.coloros.launcher.model.TransitionEffect
import kotlin.math.abs

/**
 * Applies ColorOS signature page transition animations to the Home Screen pager.
 *
 * @param pageOffset Distance of this page from the currently centered page
 *                   (-1.0 = one page to the left, 0.0 = centered, +1.0 = one page to the right).
 * @param effect The transition effect chosen by the user in settings.
 */
fun Modifier.colorOsPageTransition(
    pageOffset: Float,
    effect: TransitionEffect
): Modifier = this.graphicsLayer {
    // Standard camera distance for natural 3D depth perspective
    cameraDistance = 18f * density

    when (effect) {
        TransitionEffect.DEFAULT -> {
            // Damped spring linear slide with subtle scale elevation
            val scale = 0.96f + (1f - abs(pageOffset)) * 0.04f
            scaleX = scale
            scaleY = scale
            alpha = (1f - abs(pageOffset) * 0.4f).coerceIn(0f, 1f)
        }

        TransitionEffect.CUBE -> {
            // ColorOS 3D Cube: Pages rotate inward 90 degrees around their shared edge
            val angle = pageOffset * 90f
            rotationY = angle.coerceIn(-90f, 90f)

            if (pageOffset > 0) {
                // Page to the right pivots from its left vertical edge
                transformOrigin = TransformOrigin(0f, 0.5f)
            } else {
                // Page to the left pivots from its right vertical edge
                transformOrigin = TransformOrigin(1f, 0.5f)
            }

            // Depth receding effect
            val depthScale = 1f - (abs(pageOffset) * 0.12f)
            scaleX = depthScale
            scaleY = depthScale
            alpha = if (abs(pageOffset) > 0.99f) 0f else 1f
        }

        TransitionEffect.CYLINDER -> {
            // ColorOS 3D Cylinder / Roll: Pages conform to a rotating vertical cylinder
            val rollAngle = pageOffset * 48f
            rotationY = rollAngle.coerceIn(-60f, 60f)

            if (pageOffset > 0) {
                transformOrigin = TransformOrigin(0.15f, 0.5f)
            } else {
                transformOrigin = TransformOrigin(0.85f, 0.5f)
            }

            val cylinderScale = 1f - (abs(pageOffset) * 0.15f)
            scaleX = cylinderScale
            scaleY = cylinderScale
            alpha = (1f - abs(pageOffset) * 0.35f).coerceIn(0f, 1f)
        }

        TransitionEffect.FLIP -> {
            // ColorOS 3D Flip: Card flips horizontally around its vertical center
            val flipAngle = pageOffset * -180f
            rotationY = flipAngle
            transformOrigin = TransformOrigin(0.5f, 0.5f)

            // Hide backface when flipped past 90 degrees
            alpha = if (abs(pageOffset) in 0.5f..1.5f) 0f else 1f
        }

        TransitionEffect.CARD_STACK -> {
            // ColorOS Card Stack: Left page stays stacked in 3D depth while right page glides on top
            if (pageOffset < 0) {
                // Receding page behind
                val stackScale = 0.88f + (1f - abs(pageOffset)) * 0.12f
                scaleX = stackScale
                scaleY = stackScale
                translationX = -pageOffset * size.width * 0.6f
                alpha = (1f - abs(pageOffset) * 0.6f).coerceIn(0.2f, 1f)
            } else {
                // Top gliding card
                scaleX = 1f
                scaleY = 1f
                alpha = 1f
            }
        }

        TransitionEffect.AQUAMORPHIC_WAVE -> {
            // Water-like organic ripple slide with damped elastic squash
            val waveScale = 1f - (abs(pageOffset) * 0.10f)
            scaleX = waveScale
            scaleY = waveScale
            rotationZ = pageOffset * 3.5f // subtle organic liquid tilt
            alpha = (1f - abs(pageOffset) * 0.45f).coerceIn(0f, 1f)
        }
    }
}
