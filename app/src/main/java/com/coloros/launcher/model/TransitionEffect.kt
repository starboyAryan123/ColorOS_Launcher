package com.coloros.launcher.model

/**
 * ColorOS Signature Home Screen Transition Animations.
 * These transitions mimic the classic and modern Aquamorphic page animations
 * found in OPPO ColorOS 13, 14, and 15.
 */
enum class TransitionEffect(val title: String, val description: String) {
    DEFAULT("Default", "Fluid linear slide with damped spring deceleration"),
    CUBE("3D Cube", "Screens rotate inward like faces of a 3D perspective cube"),
    CYLINDER("3D Cylinder (Roll)", "Screens roll smoothly around a virtual cylinder with depth perspective"),
    FLIP("3D Flip", "Page flips 180 degrees with dynamic edge shadow"),
    CARD_STACK("Card Stack", "Previous page recedes into background depth while new page slides over"),
    AQUAMORPHIC_WAVE("Aquamorphic Wave", "Water-ripple staggered parallax where icons float at dynamic velocities")
}
