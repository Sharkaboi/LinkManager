package com.cybershark.linkmanager.di

import android.content.Context
import androidx.room.Room
import com.cybershark.linkmanager.data.repositories.MainRepository
import com.cybershark.linkmanager.data.retrofit.FaviconClient
import com.cybershark.linkmanager.data.room.dao.LinkDao
import com.cybershark.linkmanager.data.room.db.LinksDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun provideMainRepository(linksDao: LinkDao, @ApplicationContext context: Context): MainRepository {
        return MainRepository(linksDao, context)
    }

    @Provides
    @Singleton
    fun provideRoomDBInstance(@ApplicationContext context: Context): LinksDB {
        return Room.databaseBuilder(context, LinksDB::class.java, LinksDB.dbName)
            .build()
    }

    @Provides
    @Singleton
    fun provideLinksDao(linksDB: LinksDB): LinkDao {
        return linksDB.getLinksDao()
    }

}