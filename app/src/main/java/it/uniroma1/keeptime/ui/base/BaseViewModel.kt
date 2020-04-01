package it.uniroma1.keeptime.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    protected val _logoutMessage = MutableLiveData<Int>()
    val logoutMessage: LiveData<Int> = _logoutMessage

    protected val _message = MutableLiveData<Int>()
    val message: LiveData<Int> = _message
}
