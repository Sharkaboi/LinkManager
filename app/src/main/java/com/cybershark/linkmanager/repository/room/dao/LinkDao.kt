package com.cybershark.linkmanager.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cybershark.linkmanager.repository.room.entities.LinkEntity

@Dao
interface LinkDao {

    //insert link entity
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLink(linkEntity: LinkEntity)

    //delete link entity
    @Query("delete from links where pk=:linkId")
    suspend fun deleteLinkById(linkId: Int)

    //update link entity
    @Update
    suspend fun updateLink(linkEntity: LinkEntity)

    //get all links
    @Query("select * from links")
    fun getAllLinks(): LiveData<List<LinkEntity>>

    //clear all links
    @Query("delete from links")
    suspend fun deleteAllLinks()

}