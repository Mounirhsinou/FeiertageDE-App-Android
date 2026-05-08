# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK's proguard-android-optimize.txt.

# Keep Gson model classes
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**

-keep class com.google.gson.** { *; }
-keep class com.feiertage.deutschland.models.** { *; }

# Keep all activities, fragments, adapters
-keep class com.feiertage.deutschland.activities.** { *; }
-keep class com.feiertage.deutschland.fragments.** { *; }
-keep class com.feiertage.deutschland.adapters.** { *; }
-keep class com.feiertage.deutschland.receivers.** { *; }
-keep class com.feiertage.deutschland.utils.** { *; }

# Keep Parcelable implementations
-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
