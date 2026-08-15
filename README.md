# 🎬 Native Reels App & Reusable Compose Reels Component

A high-performance, modern Android Reels application and reusable Jetpack Compose Reels library component built from scratch using **Jetpack Compose**, **ExoPlayer (Media3)**, and **Kotlin Flow**. Designed for seamless vertical video feed swiping, HD playback, YouTube-style tap gestures, and rich interactive social bottom sheets.

---

## 📦 How to Import & Use this Library Component in ANY Android App

You can easily copy and drop the `reels` package into **ANY Jetpack Compose Android app** in 3 simple steps:

### 1. Copy the `reels` Package
Copy the `reels` package folder (`ReelItem.kt`, `ComposeReelsFeed.kt`, `ReelPlayerManager.kt`) into your app target source directory.

### 2. Add Gradle Dependencies
Ensure your target `app/build.gradle.kts` includes AndroidX Media3 & Coil:
```kotlin
implementation("androidx.media3:media3-exoplayer:1.5.1")
implementation("androidx.media3:media3-ui:1.5.1")
implementation("io.coil-kt.coil3:coil-compose:3.1.0")
```

### 3. Call `ComposeReelsFeed` in Any Screen
```kotlin
val myReels = listOf(
    ReelItem(
        id = "1",
        videoUrl = "https://yourserver.com/video.mp4",
        thumbnailUrl = "https://yourserver.com/thumb.jpg",
        title = "Awesome Custom Reel 🎬 #Android",
        username = "karthik"
    )
)

ComposeReelsFeed(
    reels = myReels,
    onBackClick = { navController.popBackStack() }
)
```

---

## 🚀 Component Highlights & Features

- **📱 Plug-and-Play Integration**: Accepts any list of `ReelItem` objects with zero external UI coupling.
- **🎬 ExoPlayer (Media3) Pooling**: Built-in `ReelPlayerManager` handles player instance pooling, forced maximum bitrate selection (`20 Mbps`), and automatic memory disposal.
- **⏯️ YouTube-Style Tap Controls**:
  - Single-tap anywhere on the screen toggles **Play ↔ Pause** with tactile **Haptic Feedback**.
  - Centered vertical controls stack with **Previous `[🔼]`**, **Play/Pause `[⏯️]`**, and **Next `[🔽]`** buttons.
  - Auto-fading controls overlay (fades after 2 seconds during playback).
- **👍 Thumbs-Up Double-Tap Like System**:
  - Double-tap anywhere to trigger a spring-animated **Thumbs Up 👍** pop-up.
  - Interactive social bar with like counts, comment counts, and share options.
- **💬 Interactive Bottom Sheets**:
  - **Comments Sheet**: Real-time comment posting under user profile `@karthik`.
  - **Share Sheet**: Native Android intent launcher for sharing reels to external apps.
- **👤 Custom Creator Profile Integration**: Built-in user avatar and personalized audio attribution.
- **🎨 Edge-to-Edge Dark Mode Visuals**: Custom dark theme with vibrant emerald accents (`BrandEmerald`).

---

## 🛠️ Technology Stack

- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose with Material 3 Design
- **Architecture**: MVI / MVVM powered by Airbnb Mavericks
- **Media Engine**: AndroidX Media3 / ExoPlayer 1.x
- **Dependency Injection**: Hilt / Dagger
- **Asynchronous Processing**: Kotlin Coroutines & Flow
- **Image Loading**: Coil 3
- **Network Stack**: Retrofit 2 + OkHttp 4 + kotlinx.serialization

---

## 📁 Architecture & Package Structure

```text
com.reelsapp
├── main
│   ├── MainActivity.kt            # Edge-to-edge Activity entry point
│   └── ReelsApp.kt                # Application class with Hilt setup
├── reels                          # 📦 REUSABLE REELS LIBRARY PACKAGE
│   ├── ReelItem.kt                # Reel data model
│   ├── ReelPlayerManager.kt       # ExoPlayer instance pool & track selector
│   ├── ComposeReelsFeed.kt        # Standalone Reusable Reels Component
│   ├── DummyReelsData.kt          # Offline media repository source
│   └── repository
│       └── ReelsRepository.kt     # Reels data layer
└── ui
    ├── screens
    │   └── HomeScreen.kt          # Mavericks ViewModel screen container
    └── theme
        ├── Color.kt               # Design system color tokens
        └── Theme.kt               # App theme setup
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- Android SDK 35 (Android 15)
- JDK 21 / 24

### Installation & Build

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/KarthikMachaiah/Reels-App.git
   cd Reels-App
   ```

2. **Open in Android Studio**:
   - Open Android Studio and choose **Open Existing Project**.
   - Select the `Reels-App` folder.

3. **Build & Run**:
   - Connect an Android device (Android 8.0 / API 26+) or launch an Emulator.
   - Run the app using `./gradlew installDebug` or click the **Run `app`** button in Android Studio.

---

## 🤝 License

Distributed under the MIT License. See `LICENSE` for more information.
