package com.cybershark.linkmanager.ui.links.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cybershark.linkmanager.repository.room.entities.LinkEntity

class LinksViewModel : ViewModel() {

    private val _linksList = MutableLiveData<List<LinkEntity>>().apply {
        value = testList()
    }

    private fun testList(): List<LinkEntity>? {
        return listOf(
            LinkEntity(1,"Reddit","https://www.reddit.com/user/SharkaBoi","https://www.google.com/s2/favicons?domain=https://www.reddit.com/user/SharkaBoi"),
            LinkEntity(2,"test","test","test")
            )
    }

    val linksList: LiveData<List<LinkEntity>> = _linksList
}