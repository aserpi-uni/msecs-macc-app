package it.uniroma1.keeptime.ui.workspaces

import androidx.lifecycle.viewModelScope
import com.android.volley.*
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.ui.base.BaseViewModel

class WorkspacesViewModel : BaseViewModel() {
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
