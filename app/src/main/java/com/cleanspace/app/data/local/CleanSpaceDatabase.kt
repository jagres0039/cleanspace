package com.cleanspace.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ScanCacheEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CleanSpaceDatabase : RoomDatabase() {
    abstract fun scanCacheDao(): ScanCacheDao
}
