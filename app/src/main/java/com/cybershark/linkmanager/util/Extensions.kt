package com.cybershark.linkmanager.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

fun MutableLiveData<UIState>.getDefault(): MutableLiveData<UIState> {
    return this.apply { value = UIState.IDLE }
}

fun MutableLiveData<UIState>.setLoading(): MutableLiveData<UIState> {
    return this.apply { value = UIState.LOADING }
}

fun MutableLiveData<UIState>.setSuccess(taskId: String, message: String): MutableLiveData<UIState> {
    return this.apply { postValue(UIState.SUCCESS(taskId, message)) }
}

fun MutableLiveData<UIState>.setError(message: String): MutableLiveData<UIState> {
    return this.apply { postValue(UIState.ERROR(message)) }
}

fun View.makeVisible() {
    this.isVisible = true
}

fun View.makeGone() {
    this.isVisible = false
}

internal fun AppCompatActivity.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()

internal fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, message, length).show()

internal fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()
