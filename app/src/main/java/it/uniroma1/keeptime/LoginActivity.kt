package it.uniroma1.keeptime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.databinding.ActivityLoginBinding
import it.uniroma1.keeptime.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import java.io.File
import java.io.ObjectInputStream


class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

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
                    val url = it.readObject() as String
                    val email = it.readObject() as String
                    val authenticationToken = it.readObject() as String

                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    LoginRepository.checkCredentials(url, email, authenticationToken,
                        viewModel::onLoginSuccess, viewModel::onLoginFailure)
                }
            }
        }
        try {
            loginWithSavedCredentials()
        } catch (error: java.io.IOException) { }

        viewModel.googleIdResult.observe(this@LoginActivity, Observer {
            val googleOauthId = it ?: return@Observer

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleOauthId)
                .requestEmail()
                .build()
            val signInIntent: Intent = GoogleSignIn.getClient(this, gso).signInIntent
            startActivityForResult(signInIntent, 9001)
        })

        googleLogin.setOnClickListener {
            val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(findViewById<ConstraintLayout>(R.id.loginContainer).windowToken, 0)
            viewModel.googleOauthId(server.toString())
        }
        googleLogin.isEnabled = false

        login.setOnClickListener {
            val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(findViewById<ConstraintLayout>(R.id.loginContainer).windowToken, 0)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            viewModel.login(email.toString(), password.toString(), server.toString())
        }

        val logoutMessage = intent.getIntExtra("message", -1)
        if(logoutMessage != -1) {
            Snackbar.make(findViewById(R.id.loginCoordinatorLayout), logoutMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun endActivity() {
        startActivity(Intent(this, NavigationDrawerActivity::class.java))
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                viewModel.googleOauthSignIn(account?.idToken!!)
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
