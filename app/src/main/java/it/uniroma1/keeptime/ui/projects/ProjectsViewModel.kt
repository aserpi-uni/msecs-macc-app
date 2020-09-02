package it.uniroma1.keeptime.ui.projects

import androidx.lifecycle.viewModelScope
import com.android.volley.*
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.ui.base.BaseViewModel

class ProjectsViewModel : BaseViewModel() {
    fun refreshProjects() = viewModelScope.launch {
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
