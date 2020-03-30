package it.uniroma1.keeptime.ui.login

import android.util.Patterns
import android.view.View
import androidx.lifecycle.*
import com.android.volley.*
import kotlinx.coroutines.launch

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository

class LoginViewModel : ViewModel() {

    val email = MutableLiveData<String>()
    val emailError: LiveData<Int> = Transformations.map(email) {
        if(it.isNullOrEmpty() || isEmailValid(it)) null else R.string.invalid_username
    }

    val password = MutableLiveData<String>()
    val passwordError: LiveData<Int> = Transformations.map(password) {
        if(it.isNullOrEmpty() || isPasswordValid(it)) null else R.string.invalid_password
    }

    val server = MutableLiveData<String>()
    val serverError: LiveData<Int> = Transformations.map(server) {
        if(it.isNullOrEmpty()) R.string.required else if(isServerValid(it)) null else R.string.invalid_server
    }

    val googleLoginable: LiveData<Boolean> = Transformations.map(server) { isServerValid(it) }

    private val _loginable = MediatorLiveData<Boolean>()
    private fun setLoginable() {
        val emailValue = email.value
        val passwordValue = password.value
        val serverValue = server.value

        _loginable.value =
            emailValue != null && isEmailValid(emailValue)
                    && passwordValue != null && isPasswordValid(passwordValue)
                    && serverValue != null && isServerValid(serverValue)
    }
    init {
        _loginable.addSource(email) { setLoginable() }
        _loginable.addSource(password) { setLoginable() }
        _loginable.addSource(server) { setLoginable() }
    }
    val loginable: LiveData<Boolean> = _loginable

    private val _busy = MutableLiveData<Boolean>()
    val busy: LiveData<Boolean> = _busy

    private val _googleOauthId = MutableLiveData<String>()
    val googleOauthId: LiveData<String> = _googleOauthId

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _message = MutableLiveData<Int>()
    val message: LiveData<Int> = _message

    fun getGoogleOauthId(view: View) {
        _busy.value = true

        viewModelScope.launch {
            try {
                _googleOauthId.value = LoginRepository.googleOauthId(server.value!!)
            } catch(error: VolleyError) {
                onLoginFailure(error)
            }
        }
    }

    fun loginWithEmail(view: View) {
        _busy.value = true
        viewModelScope.launch {
            try {
                LoginRepository.loginWithEmail(email.value!!, password.value!!, server.value!!)
                _loginSuccess.value = true
            } catch (error: VolleyError) {
                onLoginFailure(error)
            }
        }
    }

    fun googleOauthSignIn(idToken: String) {
        _busy.value = true
        viewModelScope.launch {
            try {
                LoginRepository.loginWithGoogle(idToken)
                _loginSuccess.value = true
            } catch (error: VolleyError) {
                onLoginFailure(error)
            }
        }
    }

    private fun checkCredentials() {
        _busy.value = true

        viewModelScope.launch {
            try {
                if(LoginRepository.loginWithStoredCredentials())
                    _loginSuccess.value = true
                else
                    _busy.value = false
            } catch (error: VolleyError) {
                onLoginFailure(error)
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // TODO
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 24
    }

    private fun isServerValid(server: String): Boolean {
        return Patterns.WEB_URL.matcher(server).matches()
    }

    private fun onLoginFailure(error: VolleyError) {
        _busy.value = false
        _message.value = when(error) {
            is NoConnectionError, is TimeoutError -> R.string.failed_no_response
            is AuthFailureError -> R.string.failed_wrong_credentials
            is NetworkError -> R.string.failed_network
            is ParseError, is ServerError -> R.string.failed_server
            else -> R.string.failed_unknown
        }
    }

    init {
        checkCredentials()
    }
}
