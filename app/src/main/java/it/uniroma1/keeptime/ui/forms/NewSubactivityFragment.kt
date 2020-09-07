package it.uniroma1.keeptime.ui.forms

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.icu.util.Calendar
import android.icu.util.Currency
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.databinding.NewSubactivityFragmentBinding
import it.uniroma1.keeptime.ui.base.BaseFragment
import kotlinx.android.synthetic.main.new_subactivity_fragment.view.*


class NewSubactivityFragment : BaseFragment() {

    companion object {
        fun newInstance() = NewSubactivityFragment()
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
        var date_text = view.findViewById(R.id.viewDate) as TextView
        var date_button  = view.findViewById(R.id.delivery_date_button) as Button
        var calendar:Calendar
        var datePicker:DatePickerDialog

        date_button.setOnClickListener({
            val newFragment = DatePickerFragment()
            newFragment.show(fragmentManager, "Date picker")
        })


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
    }

}
