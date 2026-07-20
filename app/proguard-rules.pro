# Add project specific ProGuard rules here.
# Keep Room entities and DAOs
-keep class com.karsaku.data.local.entity.** { *; }
-keep class com.karsaku.domain.model.** { *; }
