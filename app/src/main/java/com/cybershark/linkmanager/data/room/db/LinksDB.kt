package com.cybershark.linkmanager.data.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cybershark.linkmanager.data.room.dao.LinkDao
import com.cybershark.linkmanager.data.room.entities.LinkEntity

@Database(entities = [LinkEntity::class], exportSchema = true, version = 1)
abstract class LinksDB : RoomDatabase() {

    abstract fun getLinksDao(): LinkDao

    companion object {
        const val dbName = "links"
    }
}
