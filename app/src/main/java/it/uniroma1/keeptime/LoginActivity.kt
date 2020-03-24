package it.uniroma1.keeptime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.ui.login.LoginViewModel
import java.io.File
import java.io.ObjectInputStream


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val server = findViewById<EditText>(R.id.server)
        val googleLogin = findViewById<SignInButton>(R.id.google_login)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        fun loginWithSavedCredentials() {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            val file = File(KeepTime.context.filesDir, "CredentialsFile")
            val encryptedFile = EncryptedFile.Builder(
                file,
                KeepTime.context,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            // Read credentials from encrypted local storage
            encryptedFile.openFileInput().use { inStream ->
                ObjectInputStream(inStream).use {
                    loading.visibility = View.VISIBLE

                    val url = it.readObject() as String
                    val email = it.readObject() as String
                    val authenticationToken = it.readObject() as String

                    LoginRepository().checkCredentials(url, email, authenticationToken,
                        loginViewModel::onLoginSuccess, loginViewModel::onLoginFailure)
                }
            }
        }
        try {
            loginWithSavedCredentials()
        } catch (error: java.io.IOException) { }

        loginViewModel.googleIdResult.observe(this@LoginActivity, Observer {
            val googleOauthId = it ?: return@Observer

            loading.visibility = View.GONE
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleOauthId)
                .requestEmail()
                .build()
            val signInIntent: Intent = GoogleSignIn.getClient(this, gso).signInIntent
            startActivityForResult(signInIntent, 9001)
        })

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless form is valid
            googleLogin.isEnabled = loginState.isGoogleSignInPossible
            login.isEnabled = loginState.isDataValid

            if(loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if(loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
            if(loginState.serverError != null) {
                server.error = getString(loginState.serverError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if(loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if(loginResult.success != null) {
                endActivity()
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    server.text.toString()
            )
        }

        password.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString(),
                server.text.toString()
            )
        }

        server.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString(),
                server.text.toString()
            )
        }
        server.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    login.performClick()
                    true
                } else -> {
                    false
                }
            }
        }

        googleLogin.setOnClickListener {
            val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(findViewById<ConstraintLayout>(R.id.loginContainer).windowToken, 0)
            loading.visibility = View.VISIBLE
            loginViewModel.googleOauthId(server.text.toString())
        }
        googleLogin.isEnabled = false

        login.setOnClickListener {
            val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(findViewById<ConstraintLayout>(R.id.loginContainer).windowToken, 0)
            loading.visibility = View.VISIBLE
            loginViewModel.login(username.text.toString(), password.text.toString(), server.text.toString())
        }
    }

    private fun endActivity() {
        startActivity(Intent(this, NavigationDrawerActivity::class.java))
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Snackbar.make(
            findViewById(R.id.loginCoordinatorLayout),
            errorString,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9001) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                GoogleSignIn.getClient(this, gso).signOut()
                loginViewModel.googleOauthSignIn(account?.idToken!!)
            } catch (e: ApiException) {
                Snackbar.make(
                    findViewById(R.id.loginCoordinatorLayout),
                    R.string.failed_unknown,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
