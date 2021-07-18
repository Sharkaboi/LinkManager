package com.cybershark.linkmanager.repository

import androidx.lifecycle.LiveData
import com.cybershark.linkmanager.repository.room.entities.LinkEntity

interface IRepository {
    val allLinks: LiveData<List<LinkEntity>>

    suspend fun insertLink(linkEntity: LinkEntity): Result<Unit>

    suspend fun deleteAllLinks(): Result<Unit>

    suspend fun updateLink(linkEntity: LinkEntity): Result<Unit>

    suspend fun deleteLinkById(linkId: Int): Result<Unit>

    suspend fun getAllLinksAsString(): Result<String>
}
