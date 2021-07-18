package com.cybershark.linkmanager.util

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar

fun MutableLiveData<UIState>.getDefault(): MutableLiveData<UIState> {
    return this.apply { value = UIState.IDLE }
}

fun MutableLiveData<UIState>.setLoading(): MutableLiveData<UIState> {
    return this.apply { value = UIState.LOADING }
}

fun MutableLiveData<UIState>.setSuccess(taskId: String, message: String): MutableLiveData<UIState> {
    return this.apply { value = UIState.SUCCESS(taskId, message) }
}

fun MutableLiveData<UIState>.setError(error: Throwable?): MutableLiveData<UIState> {
    return this.apply { value = UIState.ERROR(error?.message ?: "An error occurred") }
}

internal fun AppCompatActivity.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()

internal fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, message, length).show()

fun <T> Fragment.observe(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.observe(viewLifecycleOwner) { t ->
        action(t)
    }
}

fun <T> AppCompatActivity.observe(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.observe(this) { t ->
        action(t)
    }
}

internal fun View.shortSnackBar(message: String, action: (Snackbar.() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let { snackbar.it() }
    snackbar.show()
}

internal fun Snackbar.action(message: String, action: (View) -> Unit) {
    this.setAction(message, action)
}