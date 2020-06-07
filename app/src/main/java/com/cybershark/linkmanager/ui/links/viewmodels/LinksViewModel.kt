package com.cybershark.linkmanager.ui.links.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cybershark.linkmanager.repository.Repository
import com.cybershark.linkmanager.repository.room.db.LinksDB
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LinksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository by lazy { Repository(linksDao) }
    private val linksDao by lazy { LinksDB.getDatabaseInstance(getApplication()).getDAO() }

    var linksList: LiveData<List<LinkEntity>> = repository.allLinks

    override fun onCleared() {
        super.onCleared()
        closeDB()
        Log.e("viewmodel", "onCleared:called ")
    }

    fun addLink(linkName: String, linkURL: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLink(LinkEntity(linkName = linkName, linkURL = linkURL))
        }
    }

    fun updateLink(pk: Int, linkName: String, linkURL: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLink(LinkEntity(pk, linkName, linkURL))
            Log.e("viewmodel", "updateLink: called")
        }
    }

    fun deleteLink(linkEntity: LinkEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLink(linkEntity)
            Log.e("viewmodel", "deleteLink: called")
        }
    }

    fun deleteAllLinks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllLinks()
            Log.e("viewmodel", "deleteAllLinks: deleted!")
        }
    }

    private fun closeDB() {
        LinksDB.closeDB()
    }

}