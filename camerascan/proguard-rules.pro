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
-keepattributes SourceFile,LineNumberTable
-repackageclasses 'uz.ssd.sdk.camerascan'
#-dontobfuscate
-keep class uz.ssd.sdk.camerascan.CardScanActivity

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Specifies that string constants that correspond to class names should be obfuscated as well.
-adaptclassstrings

# Note that if we could use kapt to generate registries, possible to get rid of this
-keepattributes *Annotation*
# Do not obfuscate classes with Injected Constructors

-keep class **__Factory { *; }
-keep class **__MemberInjector { *; }

-keep class com.lucem.anb.characterscanner.Scanner {*;}
-keepclassmembers  class com.lucem.anb** {*;}
