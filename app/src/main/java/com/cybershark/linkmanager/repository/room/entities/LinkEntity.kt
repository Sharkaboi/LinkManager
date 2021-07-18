package com.cybershark.linkmanager.repository.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "links")
data class LinkEntity(
    @PrimaryKey(autoGenerate = true)
    val pk: Int = 0,
    @ColumnInfo(name = "linkName", defaultValue = "")
    val linkName: String,
    @ColumnInfo(name = "linkURL", defaultValue = "")
    val linkURL: String
)