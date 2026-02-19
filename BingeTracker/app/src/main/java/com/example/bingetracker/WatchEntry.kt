package com.example.bingetracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_entries")
data class WatchEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val titleId: Long,
    val season: Int? = null,
    val episode: Int? = null,
    val note: String? = null
)
