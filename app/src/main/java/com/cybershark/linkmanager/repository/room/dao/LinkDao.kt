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
    @Delete
    suspend fun deleteLink(linkEntity: LinkEntity)

    //update link entity
    @Update
    suspend fun updateLink(linkEntity: LinkEntity)

    //get link by name
    @Query("select * from links where linkName like '%' || :linkName || '%'")
    suspend fun getLinkByName(linkName : String) : LinkEntity

    //get link by pk
    @Query("select * from links where pk=:pk")
    suspend fun getLinkByName(pk : Int) : LinkEntity

    //get all links
    @Query("select * from links")
    fun getAllLinks() : LiveData<List<LinkEntity>>

    //clear all links
    @Query("delete from links")
    suspend fun deleteAllLinks()

}