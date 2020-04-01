package it.uniroma1.keeptime.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.*

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.isUnprocessableEntity


open class BaseViewModel : ViewModel() {
    protected val _logoutMessage = MutableLiveData<Int>()
    val logoutMessage: LiveData<Int> = _logoutMessage

    protected val _message = MutableLiveData<Int>()
    val message: LiveData<Int> = _message

    protected fun volleyErrorMessage(error: VolleyError): Int {
        return if (error.isUnprocessableEntity()) R.string.failed_invalid_attribute else when (error) {
            is NoConnectionError, is TimeoutError -> R.string.failed_no_response
            is NetworkError -> R.string.failed_network
            is ParseError, is ServerError -> R.string.failed_server
            else -> R.string.failed_unknown
        }
    }
}
