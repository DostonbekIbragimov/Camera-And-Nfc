-keep class uz.ssd.sdk.nfc.NFCActivity
# Specifies that string constants that correspond to class names should be obfuscated as well.
-adaptclassstrings uz.ssd.sdk.nfc**

# Note that if we could use kapt to generate registries, possible to get rid of this
-keepattributes *Annotation*
# Do not obfuscate classes with Injected Constructors

-keep class **__Factory { *; }
-keep class **__MemberInjector { *; }


-keep class com.lucem.anb.characterscanner.Scanner {*;}
-keepclassmembers  class com.lucem.anb** {*;}