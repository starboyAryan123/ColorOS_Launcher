# Proguard rules for ColorOS Launcher
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
-dontwarn java.lang.invoke.**
