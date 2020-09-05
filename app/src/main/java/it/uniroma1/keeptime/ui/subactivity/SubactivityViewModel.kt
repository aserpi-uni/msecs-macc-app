package it.uniroma1.keeptime.ui.subactivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.*
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.Subactivity
import it.uniroma1.keeptime.data.model.SubactivityReference
import it.uniroma1.keeptime.ui.base.BaseViewModel

class SubactivityViewModel() : BaseViewModel() {
    private val _subactivity = MutableLiveData<Subactivity>()
    val activity: LiveData<Subactivity> = _subactivity

    fun refreshSubactivity(reference: SubactivityReference) = viewModelScope.launch {
        try {
            _busy.value = true
            _subactivity.value = reference.fromServer()
        } catch (error: AuthFailureError) {
            _logoutMessage.value = R.string.failed_wrong_credentials
        } catch (error: VolleyError) {
            _message.value = volleyErrorMessage(error)
        } finally {
            _busy.value = false
        }
    }
}
