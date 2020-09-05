package it.uniroma1.keeptime.ui.forms

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
import it.uniroma1.keeptime.data.model.Subactivity
import it.uniroma1.keeptime.databinding.NewSubactivityFragmentBinding

import it.uniroma1.keeptime.ui.base.BaseFragment
import kotlinx.android.synthetic.main.user_preferences_fragment.view.*

class NewSubactivity : BaseFragment() {

    companion object {
        fun newInstance() = NewSubactivity()
    }

    private lateinit var newSubactivityViewModel: NewSubactivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        newSubactivityViewModel = ViewModelProvider(this).get(NewSubactivityViewModel::class.java)
        viewModel = newSubactivityViewModel
        val binding = DataBindingUtil.inflate<NewSubactivityFragmentBinding>(
            inflater,
            R.layout.new_subactivity_fragment,
            container,
            true
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = newSubactivityViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allCurrencies = Currency.getAvailableCurrencies().toList().sortedBy { it.displayName }
        var selectedIdx = allCurrencies.indexOfFirst { it == (LoginRepository.user.value as Worker).currency }
        view.prompt_currency.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.select_currency)
                .setSingleChoiceItems(allCurrencies.map { it.displayName }.toTypedArray(), selectedIdx) { _, idx ->
                    selectedIdx = idx
                }
                .setPositiveButton(R.string.save) { _, _ -> newSubactivityViewModel.setCurrency(allCurrencies[selectedIdx]) }
                .setNegativeButton(R.string.cancel, null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menu.findItem(R.id.action_settings).isVisible = false

        val logoutItem = menu.findItem(R.id.action_logout)
        logoutItem.isVisible = true
        logoutItem.setOnMenuItemClickListener {
            newSubactivityViewModel.logout()
            true
        }
    }
}
