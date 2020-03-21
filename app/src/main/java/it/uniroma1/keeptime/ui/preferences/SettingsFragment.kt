package it.uniroma1.keeptime.ui.preferences

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository

// TODO: do not crash when user goes back and request has not finished
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        preferenceManager.findPreference<Preference>("logout")?.setOnPreferenceClickListener {
            //(activity as SettingsActivity).onLogoutClicked()
            //LoginRepository().logout(::onLogoutSuccess, ::onLogoutFailure)
            true
        }

        preferenceManager.findPreference<Preference>("user_preference")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUserPreferenceFragment())
            true
        }
    }

    private fun onLogoutSuccess() { //(activity as SettingsActivity).onLogoutSuccess()
        }

    private fun onLogoutFailure(error: VolleyError?) {
        val errorMessage = when (error) {
            is NoConnectionError, is TimeoutError -> R.string.failed_no_response
            is NetworkError -> R.string.failed_network
            else -> null
        }

        if (errorMessage != null) {
        } //(activity as SettingsActivity).onLogoutFailure(errorMessage)
        else onLogoutSuccess()
    }
}
