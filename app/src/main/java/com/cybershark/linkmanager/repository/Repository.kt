package com.cybershark.linkmanager.repository

import androidx.lifecycle.LiveData
import com.cybershark.linkmanager.repository.room.dao.LinkDao
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val linksDao: LinkDao) : IRepository {

    override val allLinks: LiveData<List<LinkEntity>> = linksDao.getAllLinks()

    override suspend fun insertLink(linkEntity: LinkEntity): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                linksDao.insertLink(linkEntity)
                return@withContext Result.success(Unit)
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun deleteAllLinks(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            linksDao.deleteAllLinks()
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun updateLink(linkEntity: LinkEntity): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                linksDao.updateLink(linkEntity)
                return@withContext Result.success(Unit)
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun deleteLinkById(linkId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            linksDao.deleteLinkById(linkId)
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun getAllLinksAsString(): Result<String> = withContext(Dispatchers.Default) {
        try {
            if (allLinks.value.isNullOrEmpty()) {
                return@withContext Result.failure(Exception("No links added!"))
            }
            val string = buildString {
                append(LinksViewModel.START_MESSAGE)
                allLinks.value?.forEach {
                    append("${it.linkName} - ${it.linkURL}\n")
                }
            }
            return@withContext Result.success(string)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}
