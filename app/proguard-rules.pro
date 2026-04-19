# SLF4J rules
-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**

# Ktor rules
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Serialization rules
-keepattributes *Annotation*, Enums
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Room rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Hilt rules
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Media3 rules
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# FFmpeg rules
-keep class com.antonkarpenko.ffmpegkit.** { *; }
-dontwarn com.antonkarpenko.ffmpegkit.**
