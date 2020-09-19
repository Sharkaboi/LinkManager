package com.cybershark.linkmanager.data.repositories

import android.content.Context
import com.cybershark.linkmanager.data.room.dao.LinkDao
import com.cybershark.linkmanager.data.room.entities.LinkEntity

class MainRepository
constructor(
    private val linksDao: LinkDao,
    context: Context
) {

    val allLinks = linksDao.getAllLinks()

    suspend fun insertLink(linkEntity: LinkEntity) = linksDao.insertLink(linkEntity)

    suspend fun deleteAllLinks() = linksDao.deleteAllLinks()

    suspend fun updateLink(linkEntity: LinkEntity) = linksDao.updateLink(linkEntity)

    suspend fun deleteLink(linkEntity: LinkEntity) = linksDao.deleteLink(linkEntity)
}
