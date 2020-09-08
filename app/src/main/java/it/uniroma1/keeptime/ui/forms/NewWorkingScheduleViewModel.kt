package it.uniroma1.keeptime.ui.forms

import android.icu.util.Currency
import android.util.Patterns
import android.view.View
import androidx.lifecycle.*
import com.android.volley.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import it.uniroma1.keeptime.KeepTime

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest
import it.uniroma1.keeptime.data.DateSerializer
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.ui.base.BaseViewModel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.coroutines.resumeWithException

class NewWorkingScheduleViewModel : BaseViewModel() {
    val _subactivityUrl = MutableLiveData<String>()
    val subactivityUrl:LiveData<String> = _subactivityUrl
    val _date = MutableLiveData<Date>()
    val date = Transformations.map(_date) {it.toString()}
    val _dateError = MediatorLiveData<Int>()
    private fun setDateError() {
        if(date.value == null) {
            _dateError.value =  R.string.invalid_date
        } else {
            _dateError.value = null
        }
    }
    init {
        _dateError.addSource(date) { setDateError()}
    }
    val dateError: LiveData<Int> = _dateError


    val hours = MutableLiveData<String>()
    private val _hoursError = MediatorLiveData<Int>()
    private fun setHoursError() {
        if(hours.value == null) {
            _hoursError.value =  R.string.invalid_hours
        } else {
            _hoursError.value = null
        }
    }
    init {
        _hoursError.addSource(hours) { setHoursError()}
    }
    val hoursError: LiveData<Int> = _hoursError

    private val _savable = MediatorLiveData<Boolean>()
    private fun setSavable() {
        _savable.value =
            dateError.value == null && hoursError.value == null
    }
    init {
        _savable.addSource(hoursError) { setSavable() }
    }
    val savable: LiveData<Boolean> = _savable


    fun createWorkingSchedule(view: View) {
        val workingScheduleParams = workingScheduleParams()
        if(workingScheduleParams.length() == 0) {
            _message.value =R.string.missing_inputs
            return
        }
        val url = "/workingschedules.json"
        _busy.value = true

        viewModelScope.launch {
            try {
                newWorkingSchedule(url, workingScheduleParams)
                _message.value = R.string.success_create
            } catch (_: AuthFailureError) {
                _logoutMessage.value = R.string.failed_wrong_credentials
            } catch (error: VolleyError) {
                _message.value = volleyErrorMessage(error)
            } finally {
                _busy.value = false
            }
        }
    }
    private fun workingScheduleParams():JSONObject{
        val workingScheduleParams = JSONObject()
        workingScheduleParams.accumulate("date", Json.stringify(DateSerializer, _date.value!!))
        workingScheduleParams.accumulate("hours", hours.value)
        workingScheduleParams.accumulate("subactivity_url", subactivityUrl.value!!.dropLast(5))
        val new = JSONObject()
        new.accumulate("workingschedule", workingScheduleParams)
        return new
    }

    private suspend fun newWorkingSchedule(url:String, payload:JSONObject):Unit = suspendCancellableCoroutine {cont->
        val request = AuthenticatedJsonObjectRequest(
            Request.Method.POST, url, payload,
            Response.Listener {
                cont.resume(Unit){}
            },
            Response.ErrorListener { cont.resumeWithException(it) }
        )
        KeepTime.instance.requestQueue.add(request)
        cont.invokeOnCancellation{request.cancel()}
    }

    // TODO
    private fun isHoursValid(hours: String): Boolean {
        if(hours.isEmpty()) return true
        return false
    }
}
