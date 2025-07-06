# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Jetpack Navigation core classes
-keep class androidx.navigation.** { *; }
-keep class * extends androidx.navigation.NavDestination
-keep class * extends androidx.navigation.Navigator

# Keep your own navigation destination sealed classes (adjusted to your actual package)
-keep class com.shanudevcodes.newsbits.data.Destination { *; }
-keep class com.shanudevcodes.newsbits.data.Destination$* { *; }

# Keep Jetpack Compose runtime and UI (especially needed for animations, transitions)
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.animation.** { *; }
-keep class androidx.compose.ui.** { *; }

# Keep navigation-compose classes
-keep class androidx.navigation.compose.** { *; }

# Keep Composable annotations (needed for navigation graph generation)
-keep @androidx.compose.runtime.Composable class * { *; }

# Keep your navigation drawer animation code
-keep class com.shanudevcodes.newsbits.ui.** { *; }
