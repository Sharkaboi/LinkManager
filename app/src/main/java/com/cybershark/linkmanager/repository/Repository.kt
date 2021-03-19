package com.cybershark.linkmanager.repository

import com.cybershark.linkmanager.repository.room.dao.LinkDao
import com.cybershark.linkmanager.repository.room.entities.LinkEntity

class Repository(private val linksDao: LinkDao) {

    val allLinks = linksDao.getAllLinks()

    suspend fun insertLink(linkEntity: LinkEntity) = linksDao.insertLink(linkEntity)

    suspend fun deleteAllLinks() = linksDao.deleteAllLinks()

    suspend fun updateLink(linkEntity: LinkEntity) = linksDao.updateLink(linkEntity)

    suspend fun deleteLinkById(linkId: Int) = linksDao.deleteLinkById(linkId)
}
