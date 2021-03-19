package com.cybershark.linkmanager.di

import android.content.Context
import androidx.room.Room
import com.cybershark.linkmanager.repository.Repository
import com.cybershark.linkmanager.repository.room.dao.LinkDao
import com.cybershark.linkmanager.repository.room.db.LinksDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getRoomDB(@ApplicationContext context: Context): LinksDB {
        return Room.databaseBuilder(context, LinksDB::class.java, "links")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun getRoomDao(linksDB: LinksDB): LinkDao {
        return linksDB.getDAO()
    }

    @Singleton
    @Provides
    fun getRepository(linkDao: LinkDao): Repository {
        return Repository(linkDao)
    }
}