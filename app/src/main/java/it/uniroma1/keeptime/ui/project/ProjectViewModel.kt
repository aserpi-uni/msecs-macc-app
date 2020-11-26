package it.uniroma1.keeptime.ui.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.*
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.Project
import it.uniroma1.keeptime.data.model.ProjectReference
import it.uniroma1.keeptime.ui.base.BaseViewModel

class ProjectViewModel() : BaseViewModel() {
    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project> = _project

    fun refreshProject(reference: ProjectReference) = viewModelScope.launch {
        try {
            _busy.value = true
            _project.value = reference.fromServer()
        } catch (error: AuthFailureError) {
            _logoutMessage.value = R.string.failed_wrong_credentials
        } catch (error: VolleyError) {
            _message.value = volleyErrorMessage(error)
        } finally {
            _busy.value = false
        }
    }
}
