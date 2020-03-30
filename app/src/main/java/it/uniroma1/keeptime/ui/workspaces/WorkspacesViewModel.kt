package it.uniroma1.keeptime.ui.workspaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.*
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository

class WorkspacesViewModel : ViewModel() {
    private val _logoutMessage = MutableLiveData<Int>()
    val logoutMessage: LiveData<Int> = _logoutMessage

    private val _message = MutableLiveData<Int>()
    val message: LiveData<Int> = _message

    fun refreshWorkspaces() = viewModelScope.launch {
        try {
            LoginRepository.refreshUser()
        } catch (error: AuthFailureError) {
            _logoutMessage.value = R.string.failed_wrong_credentials
        } catch (error: VolleyError) {
            _message.value = when(error) {
                is NoConnectionError, is TimeoutError -> R.string.failed_no_response
                is NetworkError -> R.string.failed_network
                is ParseError, is ServerError -> R.string.failed_server
                else -> R.string.failed_unknown
            }
        }
    }
}
