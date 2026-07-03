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

# Keep generic type information (required for Gson TypeToken)
-keepattributes Signature

# Keep runtime annotations
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Keep your backup model
-keep class com.hisuperaman.wallety.data.database.BackupData { *; }

# Keep your Room models
-keep class com.hisuperaman.wallety.data.model.** { *; }

-dontwarn com.google.gson.**