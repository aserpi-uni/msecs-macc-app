package it.uniroma1.keeptime.ui.projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.*
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Workspace
import it.uniroma1.keeptime.data.model.WorkspaceReference
import it.uniroma1.keeptime.ui.base.BaseViewModel

class ProjectsViewModel : BaseViewModel() {
    private val _workspace = MutableLiveData<Workspace>()
    val workspace: LiveData<Workspace> = _workspace

    fun refreshProjects(workspaceReference: WorkspaceReference?) = viewModelScope.launch {
        try {
            _busy.value = true
            if(workspaceReference != null) {
                _workspace.value = workspaceReference.fromServer()
            } else {
                LoginRepository.refreshUser()
            }
        } catch (error: AuthFailureError) {
            _logoutMessage.value = R.string.failed_wrong_credentials
        } catch (error: VolleyError) {
            _message.value = volleyErrorMessage(error)
        } finally {
            _busy.value = false
        }
    }
}
