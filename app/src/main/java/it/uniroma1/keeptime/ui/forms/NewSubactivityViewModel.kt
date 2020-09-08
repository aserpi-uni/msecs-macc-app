package it.uniroma1.keeptime.ui.forms
import android.view.View
import androidx.lifecycle.*
import com.android.volley.*
import com.google.android.material.datepicker.MaterialDatePicker
import it.uniroma1.keeptime.KeepTime
import kotlinx.coroutines.launch
import org.json.JSONObject

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.AuthenticatedJsonArrayRequest
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest
import it.uniroma1.keeptime.data.DateSerializer
import it.uniroma1.keeptime.data.model.WorkerReference
import it.uniroma1.keeptime.ui.base.BaseViewModel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.coroutines.resumeWithException

class NewSubactivityViewModel : BaseViewModel() {
    private val _workers = MutableLiveData<List<WorkerReference>>()
    val workers : LiveData<List<WorkerReference>> = _workers

    val description = MutableLiveData<String>()
    val descriptionError: LiveData<Int> = Transformations.map(description) {
        if(it.isEmpty()) R.string.invalid_description else null
    }

    val _deliveryDate = MutableLiveData<Date>(Date(MaterialDatePicker.todayInUtcMilliseconds()))
    val deliveryDate = Transformations.map(_deliveryDate) {it.toString()}
    private val _worker_1 = MutableLiveData<WorkerReference>()
    val worker_1:LiveData<String> = Transformations.map(_worker_1){it.email ?: ""}
    fun setWorker1(worker_1:WorkerReference){
        _worker_1.value = worker_1
    }
    private val _worker_2 = MutableLiveData<WorkerReference>()
    val worker_2:LiveData<String> = Transformations.map(_worker_2){it.email ?: ""}
    fun setWorker2(worker_2:WorkerReference){
        _worker_2.value = worker_2
    }
    private val _worker_3 = MutableLiveData<WorkerReference>()
    val worker_3:LiveData<String> = Transformations.map(_worker_3){it.email ?: ""}
    fun setWorker3(worker_3:WorkerReference){
        _worker_3.value = worker_3
    }

    private val _workerError = MediatorLiveData<Int>()
    private fun setWorkerError() {
        if (worker_1.value == worker_2.value || worker_1.value == worker_3.value || worker_2.value == worker_3.value) {
            _workerError.value = R.string.invalid_worker
        } else {
            _workerError.value = null
        }
    }
    init {
        _workerError.addSource(worker_1) { setWorkerError() }
        _workerError.addSource(worker_2) { setWorkerError() }
        _workerError.addSource(worker_3) { setWorkerError() }
    }

    val workerError: LiveData<Int> = _workerError

    private val _deliveryDateError = MediatorLiveData<Int>()
    private fun setDeliveryDateError() {
        if (deliveryDate.value == null){
            _deliveryDateError.value = R.string.invalid_date
        }
    }
    init{
        _deliveryDateError.addSource(deliveryDate) {setDeliveryDateError()}
    }
    val deliveryDateError: LiveData<Int> = _deliveryDateError

    val finished = MutableLiveData<Boolean>(false)

    private val _savable = MediatorLiveData<Boolean>()
    private fun setSavable() {
        _savable.value = workerError.value == null && descriptionError.value == null
    }

    init {
        _savable.addSource(workerError) { setSavable() }
        _savable.addSource(deliveryDateError) { setSavable() }
    }

    val savable = _savable

    var baseUrl: String = ""

    fun createSubactivity(view: View) {
        val subactivityParams = subactivityParams()
        _busy.value = true
        val url = baseUrl.dropLast(5) + "/subactivities.json"

        viewModelScope.launch {
            try {
                newSubactivity(url, subactivityParams)
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

    private fun subactivityParams(): JSONObject {
        val subactivityParams = JSONObject()
        subactivityParams.accumulate("description", description.value)
        subactivityParams.accumulate("delivery_time", Json.stringify<Date>(DateSerializer, _deliveryDate.value!!))
        subactivityParams.accumulate("worker_1_id", _worker_1.value!!.url)
        subactivityParams.accumulate("worker_2_id", _worker_2.value!!.url)
        subactivityParams.accumulate("worker_3_id", _worker_3.value!!.url)
        subactivityParams.accumulate("status", if(finished.value!!) "finished" else "ongoing")

        val new = JSONObject()
        new.accumulate("subactivity", subactivityParams)
        return new
    }
    fun getWorkerIds(urlString: String) = viewModelScope.launch {
        try {
            _busy.value = true
            _workers.value = getWorkers(urlString)
        } catch (error:AuthFailureError){
            _logoutMessage.value = R.string.failed_wrong_credentials
        } catch(error:VolleyError){
            _message.value = volleyErrorMessage(error)
        }finally {
            _busy.value = false
        }
    }

    //function to invoke the get_worker_ids method from the server
    private suspend fun getWorkers(url:String):List<WorkerReference> = suspendCancellableCoroutine{ cont ->
        val request = AuthenticatedJsonArrayRequest(
            Request.Method.GET, url,null,
            Response.Listener {
                cont.resume(Json.parse(ListSerializer(WorkerReference.serializer()), it.toString())) { }
            },
        Response.ErrorListener{cont.resumeWithException(it)}

        )
       KeepTime.instance.requestQueue.add(request)
        cont.invokeOnCancellation { request.cancel() }
    }

    private suspend fun newSubactivity(url:String, payload: JSONObject):Unit = suspendCancellableCoroutine{ cont ->
        val request = AuthenticatedJsonObjectRequest(
            Request.Method.POST, url, payload,
            Response.Listener {
                cont.resume(Unit) { }
            },
            Response.ErrorListener{cont.resumeWithException(it)}

        )
        KeepTime.instance.requestQueue.add(request)
        cont.invokeOnCancellation { request.cancel() }
    }

}
