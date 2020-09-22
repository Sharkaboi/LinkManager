package com.cybershark.linkmanager.ui.links.viewmodels

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.cybershark.linkmanager.data.repositories.MainRepository
import com.cybershark.linkmanager.data.room.entities.LinkEntity
import com.cybershark.linkmanager.util.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LinksViewModel
@ViewModelInject
constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val mainRepository: MainRepository
) : ViewModel() {

    var linksList: LiveData<List<LinkEntity>> = mainRepository.allLinks
    private val _uiState = MutableLiveData<UIState>().apply { value = UIState.IDLE }
    val uiState: LiveData<UIState> = _uiState

    fun addLink(linkName: String, linkURL: String) {
        _uiState.value = UIState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            val result = mainRepository.insertLink(LinkEntity(linkName = linkName, linkURL = linkURL))
            if(re)
        }
    }

    fun updateLink(newItem: LinkEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.updateLink(newItem)
            Log.e("viewmodel", "updateLink: called")
        }
    }

    fun deleteLink(linkEntity: LinkEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.deleteLink(linkEntity)
            Log.e("viewmodel", "deleteLink: called")
        }
    }

    fun deleteAllLinks() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.deleteAllLinks()
            Log.e("viewmodel", "deleteAllLinks: deleted!")
        }
    }

    fun getLinkByPK(pk: Int): LinkEntity? {
        return linksList.value?.first { linkEntity ->
            linkEntity.pk == pk
        }
    }
}