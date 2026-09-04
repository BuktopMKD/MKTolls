# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **_ViewBinding { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Dagger 2
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class *__Factory { *; }
-keep class *__MemberInjector { *; }
-keep class *__Module { *; }

# Retrofit / OkHttp / Gson
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class com.google.gson.** { *; }
-keep class com.denofdevelopers.mktolls.model.** { *; }

# RxJava
-dontwarn rx.internal.util.unsafe.**
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

# Picasso
-dontwarn com.squareup.picasso.**
-keep class com.squareup.picasso.** { *; }

# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-keep class com.google.android.gms.common.api.** { *; }

# Calligraphy
-dontwarn uk.co.chrisjenx.calligraphy.**
-keep class uk.co.chrisjenx.calligraphy.** { *; }
