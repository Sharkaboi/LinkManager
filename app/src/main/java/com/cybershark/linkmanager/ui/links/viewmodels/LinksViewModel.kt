package com.cybershark.linkmanager.ui.links.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybershark.linkmanager.repository.IRepository
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.links.views.LinksFragment
import com.cybershark.linkmanager.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinksViewModel
@Inject constructor(
    private val repository: IRepository
) : ViewModel() {

    val linksList: LiveData<List<LinkEntity>> = repository.allLinks
    private val _uiState: MutableLiveData<UIState> = MutableLiveData<UIState>().getDefault()
    val uiState: LiveData<UIState> = _uiState

    fun addLink(linkName: String, linkURL: String) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = repository.insertLink(LinkEntity(linkName = linkName, linkURL = linkURL))
            if (result.isSuccess) {
                _uiState.setSuccess(LinksFragment.INSERT_ID, "Inserted")
            } else {
                _uiState.setError(result.exceptionOrNull())
            }
        }
    }

    fun updateLink(pk: Int, linkName: String, linkURL: String) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = repository.updateLink(LinkEntity(pk, linkName, linkURL))
            if (result.isSuccess) {
                _uiState.setSuccess(LinksFragment.UPDATE_ID, "Updated")
            } else {
                _uiState.setError(result.exceptionOrNull())
            }
        }
    }

    fun deleteLink(linkId: Int) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = repository.deleteLinkById(linkId)
            if (result.isSuccess) {
                _uiState.setSuccess(LinksFragment.DELETE_ID, "Deleted")
            } else {
                _uiState.setError(result.exceptionOrNull())
            }
        }
    }

    fun deleteAllLinks() {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = repository.deleteAllLinks()
            if (result.isSuccess) {
                _uiState.setSuccess(LinksFragment.DELETE_ALL_ID, "Deleted")
            } else {
                _uiState.setError(result.exceptionOrNull())
            }
        }
    }

    fun getAllLinksAsString(taskID: String) {
        _uiState.setLoading()
        viewModelScope.launch {
            val result = repository.getAllLinksAsString()
            if (result.isSuccess) {
                _uiState.setSuccess(taskID, result.getOrDefault(""))
            } else {
                _uiState.setError(result.exceptionOrNull())
            }
        }
    }

    companion object {
        const val START_MESSAGE = "Hey Check me out here : \n"
    }
}