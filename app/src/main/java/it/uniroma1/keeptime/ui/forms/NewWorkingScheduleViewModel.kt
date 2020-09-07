package it.uniroma1.keeptime.ui.forms

import android.icu.util.Currency
import android.util.Patterns
import android.view.View
import androidx.lifecycle.*
import com.android.volley.*
import kotlinx.coroutines.launch
import org.json.JSONObject

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.ui.base.BaseViewModel

class NewWorkingScheduleViewModel : BaseViewModel() {

    val date = MutableLiveData<String>()
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

    fun logout() {
        _busy.value = true
        viewModelScope.launch {
            try {
                LoginRepository.logout()
                _logoutMessage.value = R.string.success_logout
            } catch (error: VolleyError) {
                val errorMessage = when(error) {
                    is NoConnectionError, is TimeoutError -> R.string.failed_no_response
                    is NetworkError -> R.string.failed_network
                    else -> null
                }

                if(errorMessage == null) {
                    _logoutMessage.value = R.string.success_logout
                } else {
                    _busy.value = false
                    _message.value = errorMessage
                }
            }
        }
    }

    fun updateUser(view: View) {
        val userParams = userParams()
        if(userParams.length() == 0) {
            _message.value = R.string.failed_unchanged
            return
        }
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

    private fun isEmailValid(email: String): Boolean {
        return email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // TODO
    private fun isPasswordValid(password: String): Boolean {
        return password.isEmpty() || password.length >= 24
    }

    private fun isBillRateValid(billRate: String): Boolean {
        if(billRate.isEmpty()) return true

        val billRateNumber = billRate.toDoubleOrNull() ?: return false
        return billRateNumber.round(2) == billRateNumber
    }

    private fun userParams(): JSONObject {
        val userParams = JSONObject()

        return userParams
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}
