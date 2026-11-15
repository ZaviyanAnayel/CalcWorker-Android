# Keep model/data and calculator classes
-keep class com.zaviyanllc.calcworker.data.** { *; }
-keep class com.zaviyanllc.calcworker.calc.** { *; }
-keep class com.zaviyanllc.calcworker.ui.** { *; }

# AndroidX DataStore (Prevents NoClassDefFoundError on release builds)
-keep class androidx.datastore.** { *; }
-keepclassmembers class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# OkHttp3 & Okio (Used in FxRepository for live currency exchange rates)
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { *; }

# Lifecycle & ViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
