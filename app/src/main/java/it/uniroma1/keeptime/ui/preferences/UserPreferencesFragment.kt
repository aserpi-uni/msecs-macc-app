package it.uniroma1.keeptime.ui.preferences

import android.content.Context
import android.icu.util.Currency
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.databinding.UserPreferencesFragmentBinding
import it.uniroma1.keeptime.ui.base.BaseFragment
import kotlinx.android.synthetic.main.user_preferences_fragment.view.*

class UserPreferencesFragment : BaseFragment() {

    companion object {
        fun newInstance() = UserPreferencesFragment()
    }

    private lateinit var userPreferencesViewModel: UserPreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        userPreferencesViewModel = ViewModelProvider(this).get(UserPreferencesViewModel::class.java)
        viewModel = userPreferencesViewModel
        val binding = DataBindingUtil.inflate<UserPreferencesFragmentBinding>(
            inflater,
            R.layout.user_preferences_fragment,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = userPreferencesViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferencesViewModel.busy.observe(viewLifecycleOwner, Observer {
            val busy = it ?: return@Observer
            if(! busy) return@Observer

            val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputManager?.hideSoftInputFromWindow(view.windowToken, 0)
        })

        val allCurrencies = Currency.getAvailableCurrencies().toList().sortedBy { it.displayName }
        var selectedIdx = allCurrencies.indexOfFirst { it == (LoginRepository.user.value as Worker).currency }
        view.prompt_currency.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.select_currency)
                .setSingleChoiceItems(allCurrencies.map { it.displayName }.toTypedArray(), selectedIdx) { _, idx ->
                    selectedIdx = idx
                }
                .setPositiveButton(R.string.save) { _, _ -> userPreferencesViewModel.setCurrency(allCurrencies[selectedIdx]) }
                .setNegativeButton(R.string.cancel, null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menu.findItem(R.id.action_settings).isVisible = false

        val logoutItem = menu.findItem(R.id.action_logout)
        logoutItem.isVisible = true
        logoutItem.setOnMenuItemClickListener {
            userPreferencesViewModel.logout()
            true
        }
    }
}
