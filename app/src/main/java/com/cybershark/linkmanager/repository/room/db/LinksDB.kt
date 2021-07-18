package com.cybershark.linkmanager.repository.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cybershark.linkmanager.repository.room.dao.LinkDao
import com.cybershark.linkmanager.repository.room.entities.LinkEntity

@Database(entities = [LinkEntity::class], exportSchema = true, version = 1)
abstract class LinksDB : RoomDatabase() {
    abstract fun getDAO(): LinkDao
}
