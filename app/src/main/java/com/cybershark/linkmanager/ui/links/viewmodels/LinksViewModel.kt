package com.cybershark.linkmanager.ui.links.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybershark.linkmanager.repository.Repository
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.util.UIState
import com.cybershark.linkmanager.util.getDefault
import com.cybershark.linkmanager.util.setLoading
import com.cybershark.linkmanager.util.setSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinksViewModel
@Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val startMessage = "Hey Check me out here : \n"

    val linksList: LiveData<List<LinkEntity>> = repository.allLinks
    private val _uiState: MutableLiveData<UIState> = MutableLiveData<UIState>().getDefault()
    val uiState: LiveData<UIState> get() = _uiState

    fun addLink(linkName: String, linkURL: String) {
        _uiState.setLoading()
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLink(LinkEntity(linkName = linkName, linkURL = linkURL))
            _uiState.setSuccess("INSERT", "Inserted")
        }
    }

    fun updateLink(pk: Int, linkName: String, linkURL: String) {
        _uiState.setLoading()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLink(LinkEntity(pk, linkName, linkURL))
            _uiState.setSuccess("UPDATE", "Updated")
        }
    }

    fun deleteLink(linkId: Int) {
        _uiState.setLoading()
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLinkById(linkId)
            _uiState.setSuccess("DELETE", "Deleted")
        }
    }

    fun deleteAllLinks() {
        _uiState.setLoading()
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllLinks()
            _uiState.setSuccess("DELETEALL", "Deleted")
        }
    }

    fun getAllLinksAsString(taskID: String) {
        _uiState.setLoading()
        var string: String
        viewModelScope.launch(Dispatchers.Default) {
            string = buildString {
                append(startMessage)
                linksList.value?.forEach {
                    append("${it.linkName} - ${it.linkURL}\n")
                }
            }
            _uiState.setSuccess(taskID, string)
        }
    }
}