package it.uniroma1.keeptime.ui.activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.*
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.Activity
import it.uniroma1.keeptime.data.model.ActivityReference
import it.uniroma1.keeptime.ui.base.BaseViewModel

class ActivityViewModel() : BaseViewModel() {
    private val _activity = MutableLiveData<Activity>()
    val activity: LiveData<Activity> = _activity

    fun refreshActivity(reference: ActivityReference) = viewModelScope.launch {
        try {
            _busy.value = true
            _activity.value = reference.fromServer()
        } catch (error: AuthFailureError) {
            _logoutMessage.value = R.string.failed_wrong_credentials
        } catch (error: VolleyError) {
            _message.value = volleyErrorMessage(error)
        } finally {
            _busy.value = false
        }
    }
}
