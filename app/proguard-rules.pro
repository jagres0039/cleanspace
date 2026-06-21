# ============================================================
# CleanSpace ProGuard / R8 rules
# ============================================================

-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep source/line info for readable crash stack traces, hide original names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---- Kotlin ----
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }
-keep class kotlin.Metadata { *; }

# ---- Coroutines ----
-keepclassmembernames class kotlinx.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# ---- Hilt / Dagger (mostly handled by their consumer rules; keep safe) ----
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.hilt.**

# ---- Room ----
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# ---- Google Mobile Ads (AdMob) + UMP ----
# The SDK ships its own consumer rules, but keep these to be safe.
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.ump.** { *; }
-dontwarn com.google.android.gms.ads.**

# ---- Compose ----
-dontwarn androidx.compose.**

# ---- WorkManager (Hilt worker factory uses reflection on worker names) ----
-keep class * extends androidx.work.ListenableWorker { *; }

# ---- App data/domain models (kept so any reflection/serialization is safe) ----
-keep class com.cleanspace.app.data.model.** { *; }
-keep class com.cleanspace.app.domain.model.** { *; }

# ---- Misc ----
-dontwarn org.slf4j.**
