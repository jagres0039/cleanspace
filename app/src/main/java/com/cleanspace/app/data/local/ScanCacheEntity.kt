package com.cleanspace.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_cache")
data class ScanCacheEntity(
    @PrimaryKey val path: String,
    val sizeBytes: Long,
    val hash: String?,
    val lastScanned: Long,
)
