# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# --- OkHttp rules ---
# OkHttp 5.x and Okio have built-in R8 rules, so only basic rules are needed if using ProGuard.
-keepattributes Signature, AnnotationDefault, EnclosingMethod, InnerClasses
-dontwarn okhttp3.**
-dontwarn okio.**

# --- Kotlin Serialization rules ---
# Keep serializable classes and their properties
-keepattributes *Annotation*, InnerClasses
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# --- Data Models (Specific for this project) ---
-keep class com.example.pokemonsearch.data.model.** { *; }

# --- Koin rules ---
# Koin usually doesn't need broad keep rules in modern R8.
# Keep only necessary parts if using reflection.
-dontwarn org.koin.**

# --- Glide rules ---
-keep public class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule { *; }
# Removed explicit references to GeneratedAppGlideModuleImpl and GeneratedLibraryGlideModuleImpl
# because they are generated at compile time and cause IDE errors.
# The following rule is sufficient for most cases.
-dontwarn com.bumptech.glide.**
