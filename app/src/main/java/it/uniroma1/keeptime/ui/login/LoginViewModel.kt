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

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private var loginRepository = LoginRepository()

    fun login(username: String, password: String, server: String) {
        loginRepository.login(username, password, server, ::onLoginSuccess, ::onLoginFailed)
    }

    fun loginDataChanged(username: String, password: String, server: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (!isServerValid(server)) {
            _loginForm.value = LoginFormState(serverError = R.string.invalid_server)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    // A placeholder server validation check
    private fun isServerValid(server: String): Boolean {
        return Patterns.WEB_URL.matcher(server).matches()
    }

    fun onLoginFailed(error: VolleyError) {
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
