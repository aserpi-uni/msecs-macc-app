package it.uniroma1.keeptime.ui.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.*

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.isUnprocessableEntity


/**
 * Base class for viewmodels, to be used with the [base class for fragments][BaseFragment].
 *
 * @property busy Whether the viewmodel is awaiting a network reply.
 * @property logoutMessage Message to be displayed when logging out.
 * @property message Message to be displayed in the current view.
 *
 * @property _busy See [busy].
 * @property _logoutMessage See [logoutMessage].
 * @property _message See [message].
 */
abstract class BaseViewModel : ViewModel() {
    protected val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    protected val _logoutMessage = MutableLiveData<@StringRes Int>()
    val logoutMessage: LiveData<Int> = _logoutMessage

    protected val _message = MutableLiveData<@StringRes Int>()
    val message: LiveData<Int> = _message

    /**
     * Retrieves the correct message for a Volley error.
     */
    @StringRes
    protected fun volleyErrorMessage(error: VolleyError): Int {
        return if (error.isUnprocessableEntity()) R.string.failed_invalid_attribute else when (error) {
            is NoConnectionError, is TimeoutError -> R.string.failed_no_response
            is NetworkError -> R.string.failed_network
            is ParseError, is ServerError -> R.string.failed_server
            else -> R.string.failed_unknown
        }
    }
}
