package it.uniroma1.keeptime.ui.clients

import androidx.lifecycle.viewModelScope
import com.android.volley.*
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.ui.base.BaseViewModel


class ClientsViewModel : BaseViewModel() {
    fun refreshClients() = viewModelScope.launch {
        try {
            _busy.value = true
            LoginRepository.refreshUser()
        } catch (error: AuthFailureError) {
            _logoutMessage.value = R.string.failed_wrong_credentials
        } catch (error: VolleyError) {
            _message.value = volleyErrorMessage(error)
        } finally {
            _busy.value = false
        }
    }
}
