package it.uniroma1.keeptime.ui.preferences

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

import it.uniroma1.keeptime.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        preferenceManager.findPreference<Preference>("user_preference")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUserPreferenceFragment())
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        val menuItem = menu.findItem(R.id.action_settings)
        menuItem.isVisible = false
    }
}
