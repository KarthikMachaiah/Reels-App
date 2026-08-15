# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Mavericks / MvRx state classes
-keep class * extends com.airbnb.mvrx.MavericksState { *; }
-keep class * extends com.airbnb.mvrx.MavericksViewModel { *; }

# Keep Hilt
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# Keep data classes for Room / serialization
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Media3 / ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-keep class androidx.media3.** { *; }

# Kotlin coroutines
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-keepclassmembernames class kotlinx.** { volatile <fields>; }
