package it.uniroma1.keeptime.ui.login

import android.util.Patterns
import androidx.lifecycle.*
import com.android.volley.*
import it.uniroma1.keeptime.data.LoginRepository

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.Worker

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

    private val _busy = MutableLiveData<Boolean>(false)
    val busy: LiveData<Boolean> = _busy

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

    private val _googleIdResult = MutableLiveData<String>()
    val googleIdResult: LiveData<String> = _googleIdResult

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun googleOauthId(server: String) {
        LoginRepository.googleOauthId(server, { _googleIdResult.value = it }, ::onLoginFailure)
    }

    fun googleOauthSignIn(token: String) {
        LoginRepository.googleOauthSignIn(token, ::onLoginSuccess, ::onLoginFailure)
    }

    fun login(username: String, password: String, server: String) {
        LoginRepository.login(username, password, server, ::onLoginSuccess, ::onLoginFailure)
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

    fun onLoginFailure(error: VolleyError) {
        val errorMessage = when(error) {
            is NoConnectionError, is TimeoutError -> R.string.failed_no_response
            is AuthFailureError -> R.string.failed_wrong_credentials
            is NetworkError -> R.string.failed_network
            is ParseError, is ServerError -> R.string.failed_server
            else -> R.string.failed_unknown
        }

        _loginResult.value = LoginResult(error = errorMessage)
    }

    fun onLoginSuccess(user: Worker) {
        _loginResult.value = LoginResult(success = user)
    }
}
