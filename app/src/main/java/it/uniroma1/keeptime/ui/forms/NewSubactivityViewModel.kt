package it.uniroma1.keeptime.ui.forms
import android.icu.util.Currency
import android.util.Patterns
import android.view.View
import android.view.animation.Transformation
import androidx.lifecycle.*
import com.android.volley.*
import kotlinx.coroutines.launch
import org.json.JSONObject

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.data.model.WorkerReference
import it.uniroma1.keeptime.ui.base.BaseViewModel
import java.util.*

class NewSubactivityViewModel : BaseViewModel() {
    val description = MutableLiveData<String>()
    val deliveryDate = MutableLiveData<Date>()
    val worker_1 = MutableLiveData<WorkerReference>()
    val worker_2 = MutableLiveData<WorkerReference>()
    val worker_3 = MutableLiveData<WorkerReference>()

    private val _descriptionError = MediatorLiveData<Int>()
    private fun setDescriptionError(){
        if(isDescriptionValid(description.value!!)){
            _descriptionError.value =  null}

    }
    init{
        _descriptionError.addSource(description) {setDescriptionError()}
    }
    val descriptionError: LiveData<Int> = _descriptionError

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
        if (deliveryDate == null){
            _deliveryDateError.value = R.string.invalid_date
        }
    }
    init{
        _deliveryDateError.addSource(deliveryDate) {setDeliveryDateError()}
    }
    val deliveryDateError: LiveData<Int> = _deliveryDateError


    private val _savable = MediatorLiveData<Boolean>()
    private fun setSavable() {
        _savable.value =
            workerError == null && descriptionError == null
    }

    init {
        _savable.addSource(workerError) { setSavable() }
    }
    private fun isDescriptionValid(description: String): Boolean{
        if (description.isEmpty()){return false}
        else {return true}
    }

    private fun isWorkerValid(worker: WorkerReference): Boolean {
        return worker != null
    }
    private fun isDateValid(date: Date): Boolean{
        return date != null
    }

    fun createSubactivity(view: View) {
        val userParams = subactivityParams()
        _busy.value = true

        viewModelScope.launch {
            try {
                LoginRepository.updateUser(userParams)
                _message.value = R.string.success_update
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
        val user = (LoginRepository.user.value as Worker)

        if(! description.value.isNullOrEmpty())
            subactivityParams.accumulate("description", description.value)
        if(! deliveryDate.value.isNullOrEmpty()) {
            subactivityParams.accumulate("delivery date", deliveryDate.value)
        }
        if(!worker_1.value.isNullOrEmpty()) {
            subactivityParams.accumulate("worker_1", worker_1.value)
        }
        if(!worker_2.value.isNullOrEmpty()) {
            subactivityParams.accumulate("worker_1", worker_2.value)
        }
        if(!worker_3.value.isNullOrEmpty()) {
            subactivityParams.accumulate("worker_1", worker_3.value)
        }
        return subactivityParams
    }
}