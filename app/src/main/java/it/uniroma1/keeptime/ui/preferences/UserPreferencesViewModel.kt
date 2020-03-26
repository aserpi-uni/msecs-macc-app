package it.uniroma1.keeptime.ui.preferences

import android.icu.util.Currency
import android.util.Patterns
import android.view.View
import androidx.lifecycle.*
import com.android.volley.*
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker

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

    // First element is return status (success, failure), second is possible error message
    private val _logoutResult = MutableLiveData<Pair<Boolean, Int?>>()
    val logoutResult: LiveData<Pair<Boolean, Int?>> = _logoutResult

    fun logout() {
        _busy.value = true
        LoginRepository().logout(::onLogoutSuccess, ::onLogoutFailure)
    }

    fun updateUser(v: View) {
        // TODO
    }

    private fun onLogoutFailure(error: VolleyError) {
        val errorMessage = when(error) {
            is NoConnectionError, is TimeoutError -> R.string.failed_no_response
            is NetworkError -> R.string.failed_network
            else -> null
        }

        if(errorMessage == null) onLogoutSuccess()
        else {
            _busy.value = false
            _logoutResult.value = Pair(false, errorMessage)
        }
    }

    private fun onLogoutSuccess() {
        _logoutResult.value = Pair(true, null)
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

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}
