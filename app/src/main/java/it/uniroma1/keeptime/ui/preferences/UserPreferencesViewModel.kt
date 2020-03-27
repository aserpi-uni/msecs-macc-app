package it.uniroma1.keeptime.ui.preferences

import android.icu.util.Currency
import android.util.Patterns
import android.view.View
import androidx.lifecycle.*
import com.android.volley.*
import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.isUnprocessableEntity
import it.uniroma1.keeptime.data.model.Worker
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import org.json.JSONObject
import kotlin.coroutines.resumeWithException

class UserPreferencesViewModel : ViewModel() {

    val billRate = MutableLiveData((LoginRepository.user as Worker).billRate.toString())
    val billRateError: LiveData<Int> = Transformations.map(billRate) {
        if(isBillRateValid(it)) null else R.string.invalid_bill_rate
    }

    private val _currency = MutableLiveData((LoginRepository.user as Worker).currency)
    val currency: LiveData<String> = Transformations.map(_currency) { it.displayName }
    fun setCurrency(currency: Currency) { _currency.value = currency }

    val email = MutableLiveData((LoginRepository.user as Worker).email)
    val emailError: LiveData<Int> = Transformations.map(email) {
        if(isEmailValid(it)) null else R.string.invalid_username
    }

    val password = MutableLiveData<String>()
    val passwordError: LiveData<Int> = Transformations.map(password) {
        if(isPasswordValid(it)) null else R.string.invalid_password
    }

    val passwordConfirmation = MutableLiveData<String>()
    private val _passwordConfirmationError = MediatorLiveData<Int>()
    private fun setPasswordConfirmationError() {
        if(password.value == passwordConfirmation.value) {
            _passwordConfirmationError.value =  null
        } else {
            _passwordConfirmationError.value = R.string.invalid_password_confirmation
        }
    }
    init {
        _passwordConfirmationError.addSource(password) { setPasswordConfirmationError() }
        _passwordConfirmationError.addSource(passwordConfirmation) { setPasswordConfirmationError() }
    }
    val passwordConfirmationError: LiveData<Int> = _passwordConfirmationError

    private val _message = MutableLiveData<Any>()
    val message: MutableLiveData<Any> = _message

    private val _savable = MediatorLiveData<Boolean>()
    private fun setSavable() {
        _savable.value =
            billRateError.value == null
            && emailError.value == null
            && passwordError.value == null
            && passwordConfirmationError.value == null
    }
    init {
        _savable.addSource(billRateError) { setSavable() }
        _savable.addSource(emailError) { setSavable() }
        _savable.addSource(passwordError) { setSavable() }
        _savable.addSource(passwordConfirmationError) { setSavable() }
    }
    val savable: LiveData<Boolean> = _savable

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    private val _logoutMessage = MutableLiveData<Int>()
    val logoutMessage: LiveData<Int> = _logoutMessage

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
        val requestParams = JSONObject()
        requestParams.accumulate("worker", userParams)

        viewModelScope.launch {
            try {
                LoginRepository.user = Json.parse(Worker.serializer(), sendUserUpdate(requestParams).toString())

                _busy.value = false
                _message.value = R.string.success_update
            } catch (_: AuthFailureError) {
                _logoutMessage.value = R.string.failed_wrong_credentials
            } catch (error: VolleyError) {
                val errorMessage =
                    if (error.isUnprocessableEntity()) R.string.failed_invalid_attribute else when (error) {
                        is NoConnectionError, is TimeoutError -> R.string.failed_no_response
                        is NetworkError -> R.string.failed_network
                        is ParseError, is ServerError -> R.string.failed_server
                        else -> R.string.failed_unknown
                    }

                _busy.value = false
                _message.value = errorMessage
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

    private suspend fun sendUserUpdate(params: JSONObject) = suspendCancellableCoroutine<JSONObject> { cont ->
        val request = AuthenticatedJsonObjectRequest(
            Request.Method.PATCH,
            LoginRepository.user!!.url,
            params,
            Response.Listener { cont.resume(it) { } },
            Response.ErrorListener { cont.resumeWithException(it) }
        )

        KeepTime.instance!!.requestQueue.add(request)

        cont.invokeOnCancellation {
            request.cancel()
        }
    }

    private fun userParams(): JSONObject {
        val userParams = JSONObject()
        val user = (LoginRepository.user as Worker)

        if(! email.value.isNullOrEmpty() && email.value != user.email)
            userParams.accumulate("email", email.value)
        if(! password.value.isNullOrEmpty()) {
            userParams.accumulate("password", password.value)
            userParams.accumulate("password_confirmation", password.value)
        }
        if(! billRate.value.isNullOrEmpty()) {
            val billRateNum = billRate.value!!.toDoubleOrNull()
            if(billRateNum != null && billRateNum != user.billRate)
                userParams.accumulate("bill_rate_cents", (billRateNum * 100).toInt())
        }
        if(_currency.value != user.currency)
            userParams.accumulate("currency", _currency.value!!.currencyCode)

        return userParams
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}
