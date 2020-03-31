package it.uniroma1.keeptime.ui.workspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.*
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.Workspace
import it.uniroma1.keeptime.data.model.WorkspaceReference
import it.uniroma1.keeptime.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class WorkspaceViewModel : BaseViewModel() {
    private val _busy = MutableLiveData<Boolean>()
    val busy: LiveData<Boolean> = _busy

    private val _message = MutableLiveData<Int>()
    val message: LiveData<Int> = _message

    private val _workspace = MutableLiveData<Workspace>()
    val workspace: LiveData<Workspace> = _workspace

    fun getWorkspace(reference: WorkspaceReference) = viewModelScope.launch {
        try {
            _busy.value = true
            _workspace.value = reference.fromServer()
        } catch (error: AuthFailureError) {
            _logoutMessage.value = R.string.failed_wrong_credentials
        } catch (error: VolleyError) {
            _message.value = when(error) {
                is NoConnectionError, is TimeoutError -> R.string.failed_no_response
                is NetworkError -> R.string.failed_network
                is ParseError, is ServerError -> R.string.failed_server
                else -> R.string.failed_unknown
            }
        } finally {
            _busy.value = false
        }
    }
}
