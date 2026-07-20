# Keep Room entities, DAOs, and Domain Models
-keep class com.pesanku.data.local.entity.** { *; }
-keep class com.pesanku.domain.model.** { *; }

# Keep Room generated classes
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep ViewModel factories
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
