package com.cybershark.linkmanager.util

sealed class UIState {
    object LOADING : UIState()
    object IDLE : UIState()
    data class SUCCESS(val taskId: String, val message: String) : UIState()
    data class ERROR(val message: String) : UIState()
}
