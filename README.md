# 🎬 Native Reels & AI Studio App (100% Jetpack Compose)

A modern, high-performance Android Reels application built completely from scratch using **Jetpack Compose**, **ExoPlayer (Media3)**, **Kotlin Flow**, and **Live Generative AI (Pollinations.ai / Flux)**.

Featuring a floating animated glassmorphism navigation pill, full-screen vertical video reels feed, a 150 MB LRU disk caching system for instant offline re-playback, a dedicated generative AI Studio screen, and a dark/light theme engine.

---

## 📦 How to Import & Use the Reusable Reels Library Component

You can easily drop the standalone `reels` library package into **ANY Jetpack Compose Android application**:

### 1. Copy the `reels` Package
Copy the `reels` package folder (`ReelItem.kt`, `ComposeReelsFeed.kt`, `ReelPlayerManager.kt`) directly into your project source root.

### 2. Add Gradle Dependencies
Ensure your app `build.gradle.kts` has ExoPlayer & Coil:
```kotlin
implementation("androidx.media3:media3-exoplayer:1.5.1")
implementation("androidx.media3:media3-ui:1.5.1")
implementation("io.coil-kt.coil3:coil-compose:3.1.0")
```

### 3. Render `ComposeReelsFeed` in Any Screen
```kotlin
val myReels = listOf(
    ReelItem(
        id = "1",
        videoUrl = "https://yourserver.com/reel.mp4",
        thumbnailUrl = "https://yourserver.com/thumbnail.jpg",
        title = "Modern Compose Reel 🎬 #Android",
        username = "karthik"
    )
)

ComposeReelsFeed(
    reels = myReels,
    onBackClick = { navController.popBackStack() }
)
```

---

## 🚀 Application & Architecture Highlights

### ⚡ 1. Floating Animated Glassmorphism Navigation Bar
- Modern floating pill navigation bar featuring **4 main tabs**:
  - 🏠 **Home**: Hero dashboard launcher with smooth bouncy dialog prompt.
  - 🤖 **AI Reels**: Live Generative AI Studio screen.
  - 🎬 **Just Reels**: Full-screen vertical video reels feed with auto-hiding navigation bar during playback.
  - 👤 **Profile**: Profile dashboard featuring live Light/Dark mode theme toggle.
- Tactile **Haptic Feedback** and spring-scale selection animations.

### 🧠 2. Real-Time Text-to-AI Image Generation Engine
- Powered by **Pollinations AI** (Flux.1 / Stable Diffusion models).
- Type any prompt (e.g. *"Cyberpunk City 2077"*, *"Futuristic Neon Cat"*, *"Underwater Floating City"*) to render **brand-new, high-definition 9:16 vertical AI artwork on-demand**.

### 💾 3. 150 MB LRU Disk Cache System
- Built-in `ReelPlayerManager` initializes ExoPlayer with a `SimpleCache` and `LeastRecentlyUsedCacheEvictor(150 MB)`.
- Network MP4 streams are saved locally to disk automatically for **instant 0-buffer re-playback**.

### ⏯️ 4. YouTube-Style Tap Controls & Social Interactions
- Single-tap to toggle Play ↔ Pause.
- Double-tap anywhere to trigger a spring-animated **Thumbs Up 👍** pop-up.
- Real-time **Comments Bottom Sheet** and **Native Android Share Sheet**.

---

## 📁 Repository Architecture & Package Overview

```text
com.reelsapp
├── MainActivity.kt                      # Edge-to-edge launcher Activity
├── ReelsApplication.kt                  # Application class with Hilt DI
├── reels                                # 📦 REUSABLE REELS LIBRARY PACKAGE
│   ├── ComposeReelsFeed.kt              # Main Reusable Vertical Pager Component
│   ├── ReelItem.kt                      # Data model for reels feed
│   ├── ReelPlayerManager.kt             # ExoPlayer pool & 150MB LRU disk cache
│   ├── DummyReelsData.kt                # Media assets & offline fallback data
│   ├── api/                             # Network API models
│   └── repository/                      # Media repository implementation
└── ui
    ├── home/                            # Home screen MVI state & Mavericks ViewModel
    ├── navigation/                      # AppTab enum & FloatingPillNavigationBar
    ├── screens/
    │   ├── HomeScreen.kt                # Root container & Home dashboard
    │   ├── AiReelsScreen.kt             # Live Text-to-AI Image Generation Screen
    │   └── ProfileScreen.kt             # User profile & Light/Dark Theme Switcher
    └── theme/                           # Custom design system & theme transitions
```

---

## 🛠️ Technology Stack

- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVI / MVVM (Airbnb Mavericks 3.x)
- **Media Engine**: AndroidX Media3 / ExoPlayer 1.5.1
- **AI Synthesis**: Pollinations AI (Flux.1 / Stable Diffusion)
- **Disk Caching**: Media3 `SimpleCache` + `LeastRecentlyUsedCacheEvictor` (150 MB)
- **Dependency Injection**: Hilt / Dagger
- **Image Loading**: Coil 3
- **Network Stack**: Retrofit 2 + OkHttp 4

---

## 🤝 License

Distributed under the MIT License. Built with 100% custom code.
