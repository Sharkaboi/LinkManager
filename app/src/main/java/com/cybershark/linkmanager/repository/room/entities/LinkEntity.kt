package com.cybershark.linkmanager.repository.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LinkEntity(
    @PrimaryKey
    val pk : Int ,
    val linkName : String,
    val linkURL : String,
    val linkFavIconURL : String
)