package com.cybershark.linkmanager.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cybershark.linkmanager.data.room.entities.LinkEntity

@Dao
interface LinkDao {

    //insert link entity
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLink(linkEntity: LinkEntity): Long

    //delete link entity
    @Delete
    suspend fun deleteLink(linkEntity: LinkEntity): Int

    //update link entity
    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateLink(linkEntity: LinkEntity): Int

    //get link by name
    @Query("select * from links where linkName like '%' || :linkName || '%'")
    suspend fun getLinkByName(linkName: String): LinkEntity

    //get link by pk
    @Query("select * from links where pk=:pk")
    suspend fun getLinkByID(pk: Int): LinkEntity

    //get all links
    @Query("select * from links")
    fun getAllLinks(): LiveData<List<LinkEntity>>

    //clear all links
    @Query("delete from links")
    suspend fun deleteAllLinks(): Int

}