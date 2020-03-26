package it.uniroma1.keeptime.ui.preferences

import android.app.Activity
import android.content.Intent
import android.icu.util.Currency
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import it.uniroma1.keeptime.LoginActivity

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.databinding.UserPreferencesFragmentBinding
import kotlinx.android.synthetic.main.user_preferences_fragment.view.*

class UserPreferencesFragment : Fragment() {

    companion object {
        fun newInstance() = UserPreferencesFragment()
    }

    private lateinit var viewModel: UserPreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this).get(UserPreferencesViewModel::class.java)
        val binding = DataBindingUtil.inflate<UserPreferencesFragmentBinding>(
            inflater,
            R.layout.user_preferences_fragment,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.logoutResult.observe(viewLifecycleOwner, Observer {
            val logoutResult = it ?: return@Observer

            if(logoutResult.first) {
                LoginRepository().removeCredentials()
                val loginIntent = Intent(context, LoginActivity::class.java)
                loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(loginIntent)

                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            } else {
                Snackbar.make(view, logoutResult.second!!, Snackbar.LENGTH_SHORT).show()
            }
        })

        val allCurrencies = Currency.getAvailableCurrencies().toList().sortedBy { it.displayName }
        var selectedIdx = allCurrencies.indexOfFirst { it == (LoginRepository.user as Worker).currency }
        view.prompt_currency.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.select_currency)
                .setSingleChoiceItems(allCurrencies.map { it.displayName }.toTypedArray(), selectedIdx) { _, idx ->
                    selectedIdx = idx
                }
                .setPositiveButton(R.string.save) { _, _ -> viewModel.setCurrency(allCurrencies[selectedIdx]) }
                .setNegativeButton(R.string.cancel, null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menu.findItem(R.id.action_settings).isVisible = false

        val logoutItem = menu.findItem(R.id.action_logout)
        logoutItem.isVisible = true
        logoutItem.setOnMenuItemClickListener {
            viewModel.logout()
            true
        }
    }
}
