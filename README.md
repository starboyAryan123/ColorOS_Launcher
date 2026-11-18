# ColorOS Aquamorphic Launcher for Android

A high-performance Android mobile launcher engineered with the **ColorOS Aquamorphic Design System** and **Quantum Animation Engine**, compatible with any Android smartphone running **Android 8.0 (API 26) through Android 14 & 15 (API 35)**.

---

## 🌟 Key Features & Mechanics

### 1. ColorOS Signature Depth Effects
- **Multi-Plane Wallpaper Parallax**: Device accelerometer and gyroscope motion vectors are filtered in real-time (`GyroscopeParallaxSensor.kt`) to create realistic physical depth between background wallpaper layers, midground water particles, and foreground cards/icons.
- **Continuous Squircle Curvature**: Recreates ColorOS superellipse icon geometry with soft ambient drop-shadows and top-rim specular highlights.
- **Acrylic Frosted Glass Surfaces**: Translucent frosted surfaces (`0x33FFFFFF`) with dynamic Gaussian blur across the Dock, Large Folders, App Drawer, and Search Pill.

### 2. Quantum Animation Engine Transitions
ColorOS home screen page transitions implemented via Jetpack Compose `graphicsLayer` 3D projection:
- **3D Cube**: Pages rotate 90° around their vertical edge with natural perspective foreshortening.
- **3D Cylinder (Roll)**: Pages wrap around a virtual cylinder with depth scaling.
- **3D Flip**: Pages flip 180° around the central vertical axis with edge shadow.
- **Card Stack**: Active screen recedes into 3D background depth while the incoming screen glides on top.
- **Aquamorphic Wave**: Water-ripple staggered translation where elements move at dynamic velocities.
- **Default (Slide)**: Damped harmonic spring physics.

### 3. Spatial App Launch Expansion
- Tapping any app icon triggers an expansion directly from the icon’s exact bounding box `(x, y, w, h)` to the device viewport, interpolating squircle corner radii smoothly before launching the target Activity.

### 4. ColorOS 2x2 Large Folders
- Displays 9 apps in a 3x3 mini grid.
- **Direct-Tap Launching**: Any app inside the 2x2 folder can be launched directly with a single tap without opening the folder first.
- Tapping the folder title or card expands it into an Aquamorphic frosted glass modal.

### 5. ColorOS Widgets & App Drawer
- **Aquamorphic Clock & Weather**: Typography matching OPPO Sans with live temperature & weather status pill.
- **Speed Booster / RAM Cleaner**: Interactive circular speed ring that clears background tasks and displays released memory metrics.
- **App Drawer Sheet**: Pull-up gesture drawer with real-time fuzzy search and vertical Alphabet quick-jump indexer (`A-Z`) with haptic feedback.

---

## 📁 Project Architecture

```
ColorOS_Launcher/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml              # Home launcher intent filters & permissions
│   │   ├── java/com/coloros/launcher/
│   │   │   ├── MainActivity.kt              # Main entry point & lifecycle manager
│   │   │   ├── SettingsActivity.kt          # Settings activity for transitions & depth
│   │   │   ├── model/
│   │   │   │   ├── AppItem.kt               # App metadata model
│   │   │   │   ├── FolderItem.kt            # 2x2 Large folder data model
│   │   │   │   └── TransitionEffect.kt      # Enum for 6 ColorOS transition effects
│   │   │   ├── data/
│   │   │   │   ├── AppRepository.kt         # Queries PackageManager for installed apps
│   │   │   │   └── LauncherPreferences.kt   # DataStore persistence for launcher settings
│   │   │   ├── sensor/
│   │   │   │   └── GyroscopeParallaxSensor.kt # Low-pass filtered gyroscope motion listener
│   │   │   └── ui/
│   │   │       ├── theme/                   # Aquamorphic color palette & typography
│   │   │       ├── home/                    # HomeScreen, PageTransitions, Dock, Parallax
│   │   │       ├── drawer/                  # AppDrawerSheet & AlphabetIndexer
│   │   │       ├── folder/                  # LargeFolderView & FolderExpandedDialog
│   │   │       ├── widgets/                 # Clock, SearchPill, RamCleaner
│   │   │       └── settings/                # Transition selector & customization UI
│   │   └── res/                             # Aquamorphic drawables, strings, styles
│   └── build.gradle.kts
├── preview/
│   └── index.html                           # Live interactive 3D Web Simulator
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 How to Build & Install on Any Android Phone

### Prerequisites
- Android Studio Hedgehog / Iguana / Jellyfish (or newer) **OR** JDK 17+ with Android SDK installed.

### Option A: Using Android Studio (Recommended)
1. Open Android Studio.
2. Select **Open** and choose the `ColorOS_Launcher` folder.
3. Allow Gradle to sync dependencies.
4. Connect your Android phone via USB (with **USB Debugging** enabled in Developer Options) or start an Android Virtual Device (AVD).
5. Click **Run** (Green Play button) or press `Shift + F10`.

### Option B: Build APK via Command Line
```bash
cd ColorOS_Launcher
./gradlew assembleDebug
```
The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

To install directly to a connected phone via ADB:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 How to Set as Default Launcher on Any Brand Phone

1. Once installed, press the **Home button** or perform the **Swipe to Home** gesture.
2. Your phone will prompt you to choose a home app. Select **ColorOS Launcher** and tap **Always**.
3. Alternatively, open your phone's system settings:
   - **Samsung (One UI)**: `Settings -> Apps -> Choose default apps -> Home app -> ColorOS Launcher`
   - **Xiaomi / Redmi / POCO (HyperOS / MIUI)**: `Settings -> Apps -> Manage apps -> 3 dots menu -> Default apps -> Launcher -> ColorOS Launcher`
   - **Google Pixel / Motorola / Stock Android**: `Settings -> Apps -> Default apps -> Home app -> ColorOS Launcher`
   - **OnePlus / Realme / Vivo**: `Settings -> App management -> Default apps -> Home app -> ColorOS Launcher`

---

## 🌐 Instant Interactive Web Simulator

You can immediately test the ColorOS animations, depth parallax, 3D transitions (Cube, Cylinder, Flip, Card Stack, Wave), 2x2 Large Folders, and App Drawer in your web browser:
Open `ColorOS_Launcher/preview/index.html` in Chrome, Safari, or Edge.
