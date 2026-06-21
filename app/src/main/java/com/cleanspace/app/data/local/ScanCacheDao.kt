package com.cleanspace.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ScanCacheDao {
    @Upsert suspend fun upsertAll(items: List<ScanCacheEntity>)

    @Query("SELECT * FROM scan_cache")
    suspend fun getAll(): List<ScanCacheEntity>

    @Query("DELETE FROM scan_cache")
    suspend fun clear()
}
