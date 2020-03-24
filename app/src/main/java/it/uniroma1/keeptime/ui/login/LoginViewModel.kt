package it.uniroma1.keeptime.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.android.volley.*
import it.uniroma1.keeptime.data.LoginRepository

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.Worker

class LoginViewModel : ViewModel() {

    private val _googleIdResult = MutableLiveData<String>()
    val googleIdResult: LiveData<String> = _googleIdResult

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private var loginRepository = LoginRepository()

    fun googleOauthId(server: String) {
        loginRepository.googleOauthId(server, { _googleIdResult.value = it }, ::onLoginFailure)
    }

    fun googleOauthSignIn(token: String) {
        loginRepository.googleOauthSignIn(token, ::onLoginSuccess, ::onLoginFailure)
    }

    fun login(username: String, password: String, server: String) {
        loginRepository.login(username, password, server, ::onLoginSuccess, ::onLoginFailure)
    }

    fun loginDataChanged(username: String, password: String, server: String) {
        var serverError: Int? = null
        if(! isServerValid(server)) {
            serverError =  R.string.invalid_server
        }

        if(!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(
                isGoogleSignInPossible = serverError == null,
                serverError = serverError,
                usernameError = R.string.invalid_username
            )
        } else if(!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(
                isGoogleSignInPossible = serverError == null,
                passwordError = R.string.invalid_password,
                serverError = serverError
            )
        } else if(serverError != null) {
            _loginForm.value = LoginFormState(isGoogleSignInPossible = false, serverError = serverError)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true, isGoogleSignInPossible = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    // TODO
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
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
