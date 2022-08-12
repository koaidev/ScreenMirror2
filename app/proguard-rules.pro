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

-keep class com.firebase.** { *; }

#Gson
-keepattributes Signature, *Annotation*, Exception
-keepclassmembers enum * {*;}

-dontwarn sun.misc.**
-keep class com.kunkun.videoeditor.videomaker.model.** { <fields>; }
-keep class com.google.gson.* { *; }

#-keep class com.google.gson.stream.** { *; }
# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter {*;}
-keep class * implements com.google.gson.TypeAdapterFactory {*;}
-keep class * implements com.google.gson.JsonSerializer {*;}
-keep class * implements com.google.gson.JsonDeserializer {*;}
# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

#Parce
-keep class **$$Parcelable { *; }

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

-keep,includedescriptorclasses public class com.google.android.ads.nativetemplates.** { *; }

-keep public class com.google.android.gms.ads.**{
   public *;
}
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
#-dontobfuscate
#-dontshrink
#-dontoptimize
#-dontwarn

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#--------Gson---------#
-keepattributes Signature, *Annotation*, Exception
-keepclassmembers enum * {*;}

-dontwarn sun.misc.**
-keep class com.kunkun.videoeditor.videomaker.model.** { <fields>; }
-keep class com.google.gson.* { *; }

#-keep class com.google.gson.stream.** { *; }
# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter {*;}
-keep class * implements com.google.gson.TypeAdapterFactory {*;}
-keep class * implements com.google.gson.JsonSerializer {*;}
-keep class * implements com.google.gson.JsonDeserializer {*;}
#-keep class com.google.gson.JsonParseException

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken


# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# The following rules are used to strip any non essential Google Play Services classes and method.

# For Google Play Services
-keep public class com.google.android.gms.ads.**{
   public *;
}

# For old ads classes
-keep public class com.google.ads.**{
   public *;
}


#--------End Gson---------#

#Parceler
#-keep interface org.parceler.Parcel
#-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }

# Only necessary if you downloaded the SDK jar directly instead of from maven.
-keep class com.shaded.fasterxml.jackson.** { *; }

-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}


-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class com.bangbangcoding.screenmirror.web.utils.AdsUtils {
    public static <methods>;
}



-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class com.bumptech.glide.load.resource.gif.GifDrawable
-keep class com.bumptech.glide.load.resource.gif.GifDrawable$GifState {*;}
-keepclasseswithmembernames class com.bumptech.glide.load.resource.gif.GifDrawable$GifState
-keepclasseswithmembers class com.bumptech.glide.load.resource.gif.GifDrawable$GifState
-keep class com.bumptech.glide.load.resource.gif.GifFrameLoader
-keep class com.bumptech.glide.gifdecoder.**
-keepclassmembers class com.bumptech.glide.load.resource.gif.GifFrameLoader {
    private <fields>;
}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class nl.bravobit.ffmpeg.*

-keep interface nl.bravobit.ffmpeg.*

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation


##### Google #####
# Databinding
-dontwarn android.databinding.**
# Dagger
-dontwarn com.google.errorprone.annotations.**
##################

##### OkHttp, Retrofit #####
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
#################

##### Moshi for JSON #####
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

##### gRPC #####
-dontwarn android.test.**
-dontwarn com.google.common.**
-dontwarn javax.naming.**
-dontwarn okio.**
-dontwarn org.junit.**
-dontwarn org.mockito.**
-dontwarn sun.reflect.**
# Ignores: can't find referenced class javax.lang.model.element.Modifier
-dontwarn com.google.errorprone.annotations.**
##################

##### Protobuf #####
-keep class * extends com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**
####################

##### Crashlytics #####
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
#######################

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-dontnote rx.internal.util.PlatformDependent
-keep,includedescriptorclasses public class com.google.android.ads.nativetemplates.** { *; }