package it.uniroma1.keeptime

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import it.uniroma1.keeptime.data.LoginRepository


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun onLogoutClicked() {
        findViewById<RelativeLayout>(R.id.progress_bar).visibility = View.VISIBLE
    }

    fun onLogoutFailure(@StringRes errorMessage: Int) {
        findViewById<RelativeLayout>(R.id.progress_bar).visibility = View.GONE
        Snackbar.make(findViewById(R.id.settings), errorMessage, Snackbar.LENGTH_SHORT).show()
    }

    fun onLogoutSuccess() {
        LoginRepository().removeCredentials()
        val intent = Intent(KeepTime.context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // TODO: do not crash when user goes back and request has not finished
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val button: Preference? = preferenceManager.findPreference("logout")
            button?.setOnPreferenceClickListener {
                (activity as SettingsActivity).onLogoutClicked()
                LoginRepository().logout(::onLogoutSuccess, ::onLogoutFailure)
                true
            }
        }

        private fun onLogoutSuccess() { (activity as SettingsActivity).onLogoutSuccess() }

        private fun onLogoutFailure(error: VolleyError?) {
            val errorMessage = when(error) {
                is NoConnectionError, is TimeoutError -> R.string.failed_no_response
                is NetworkError -> R.string.failed_network
                else -> null
            }

            if(errorMessage != null) (activity as SettingsActivity).onLogoutFailure(errorMessage)
            else onLogoutSuccess()
        }
    }
}
