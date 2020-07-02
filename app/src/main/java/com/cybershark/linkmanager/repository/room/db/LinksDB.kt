package com.cybershark.linkmanager.repository.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cybershark.linkmanager.repository.room.dao.LinkDao
import com.cybershark.linkmanager.repository.room.entities.LinkEntity

@Database(entities = [LinkEntity::class], exportSchema = true, version = 1)
abstract class LinksDB : RoomDatabase() {

    abstract fun getDAO(): LinkDao

    companion object {

        @Volatile
        private var linksSQLiteDB: LinksDB? = null

        fun getDatabaseInstance(applicationContext: Context): LinksDB {
            val tempInstance = linksSQLiteDB
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance =
                    Room.databaseBuilder(applicationContext, LinksDB::class.java, "links").build()
                linksSQLiteDB = instance
                return instance
            }
        }

        fun closeDB() { 
            linksSQLiteDB?.close()
            linksSQLiteDB = null
        }
    }

}
