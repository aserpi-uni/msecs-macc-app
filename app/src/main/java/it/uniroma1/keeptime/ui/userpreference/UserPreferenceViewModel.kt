package it.uniroma1.keeptime.ui.userpreference

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.*
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository

class UserPreferenceViewModel : ViewModel() {

    // First element is return status (success, failure), second is possible error message
    private val _logoutResult = MutableLiveData<Pair<Boolean, Int?>>()
    val logoutResult: LiveData<Pair<Boolean, Int?>> = _logoutResult

    fun logout() {
        LoginRepository().logout(::onLogoutSuccess, ::onLogoutFailure)
    }

    private fun onLogoutFailure(error: VolleyError) {
        val errorMessage = when(error) {
            is NoConnectionError, is TimeoutError -> R.string.failed_no_response
            is NetworkError -> R.string.failed_network
            else -> null
        }

        if(errorMessage == null) onLogoutSuccess()
        else _logoutResult.value = Pair(false, errorMessage)
    }

    private fun onLogoutSuccess() {
        _logoutResult.value = Pair(true, null)
    }
}
