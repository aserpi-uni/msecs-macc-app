package it.uniroma1.keeptime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*

import it.uniroma1.keeptime.databinding.ActivityLoginBinding
import it.uniroma1.keeptime.ui.login.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.busy.observe(this@LoginActivity, Observer {
            if(! it) return@Observer

            val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(loginContainer.windowToken, 0)
        })

        viewModel.googleOauthId.observe(this@LoginActivity, Observer {
            val googleOauthId = it ?: return@Observer

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleOauthId)
                .requestEmail()
                .build()
            val signInIntent: Intent = GoogleSignIn.getClient(this, gso).signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        })

        viewModel.loginSuccess.observe(this@LoginActivity, Observer {
            if(! it) return@Observer

            startActivity(Intent(this, NavigationDrawerActivity::class.java))
            setResult(Activity.RESULT_OK)
            finish()
        })

        viewModel.message.observe(this@LoginActivity, Observer {
            val message = it ?: return@Observer

            Snackbar.make(loginCoordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
        })

        val logoutMessage = intent.getIntExtra("message", -1)
        if(logoutMessage != -1) {
            Snackbar.make(findViewById(R.id.loginCoordinatorLayout), logoutMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                GoogleSignIn.getClient(this, gso).signOut()
                viewModel.googleOauthSignIn(account!!.idToken!!)
            } catch (e: ApiException) {
                Snackbar.make(loginCoordinatorLayout, R.string.failed_unknown, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

const val RC_SIGN_IN = 9001
