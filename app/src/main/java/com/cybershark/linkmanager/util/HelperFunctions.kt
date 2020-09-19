package com.cybershark.linkmanager.util

import android.content.Context
import android.widget.Toast
import com.cybershark.linkmanager.data.room.entities.LinkEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal fun Context.shortToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
internal fun Context.longToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

internal suspend fun List<LinkEntity>.getListAsFriendlyMessage(): String = withContext(Dispatchers.Main) {
    return@withContext buildString {
        append("Hey Check me out here : \n")
        this@getListAsFriendlyMessage.forEach {
            append("${it.linkName} - ${it.linkURL} \n")
        }
    }
}

internal fun Any?.isNotNull(): Boolean {
    return this != null
}