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

-keepattributes *Annotation*
-keepattributes Exceptions
-keep class com.immomo.mls.annotation.* { *; }

-keep @com.immomo.mls.annotation.LuaClass class * {
    @com.immomo.mls.annotation.LuaBridge <methods>;
}
-keep @com.immomo.mls.wrapper.ConstantClass class * {
    @com.immomo.mls.wrapper.Constant <fields>;
}
-keep,allowobfuscation @interface org.luaj.vm2.utils.LuaApiUsed
-keep @com.immomo.mls.annotation.CreatedByApt class * { *; }
-keep @org.luaj.vm2.utils.LuaApiUsed class *
-keep @org.luaj.vm2.utils.LuaApiUsed class * {
    native <methods>;
    @org.luaj.vm2.utils.LuaApiUsed <methods>;
    @org.luaj.vm2.utils.LuaApiUsed <fields>;
}