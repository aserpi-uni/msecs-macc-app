package it.uniroma1.keeptime.ui.subactivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.*
import it.uniroma1.keeptime.KeepTime
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest
import it.uniroma1.keeptime.data.model.Subactivity
import it.uniroma1.keeptime.data.model.SubactivityReference
import it.uniroma1.keeptime.ui.base.BaseViewModel
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resumeWithException

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

    fun finish(reference: SubactivityReference) = viewModelScope.launch {
        try {
            _busy.value = true
            val statusJson = JSONObject()
            statusJson.accumulate("status", "finished")
            val payload = JSONObject()
            payload.accumulate("subactivity", statusJson)
            finishRequest(reference.url.toString().dropLast(5) + "/update_status.json", payload)
            _subactivity.value = reference.fromServer()
        } catch (error: AuthFailureError) {
            _logoutMessage.value = R.string.failed_wrong_credentials
        } catch (error: VolleyError) {
            _message.value = volleyErrorMessage(error)
        } finally {
            _busy.value = false
        }
    }

    suspend fun finishRequest(url: String, payload: JSONObject) = suspendCancellableCoroutine<Unit> { continuation ->
        val logoutRequest = AuthenticatedJsonObjectRequest(
            Request.Method.PATCH, url, payload,
            Response.Listener { continuation.resume(Unit) { } },
            Response.ErrorListener { error -> continuation.resumeWithException(error) }
        )

        KeepTime.instance.requestQueue.add(logoutRequest)

        continuation.invokeOnCancellation {
            logoutRequest.cancel()
        }
    }
}
