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
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.databinding.NewWorkingscheduleFragmentBinding
import it.uniroma1.keeptime.databinding.UserPreferencesFragmentBinding
import it.uniroma1.keeptime.ui.base.BaseFragment
import kotlinx.android.synthetic.main.user_preferences_fragment.view.*

class NewWorkingScheduleFragment : BaseFragment() {

    companion object {
        fun newInstance() = NewWorkingScheduleFragment()
    }

    private lateinit var newWorkingScheduleViewModel: NewWorkingScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        newWorkingScheduleViewModel = ViewModelProvider(this).get(NewWorkingScheduleViewModel::class.java)
        viewModel = newWorkingScheduleViewModel
        val binding = DataBindingUtil.inflate<NewWorkingscheduleFragmentBinding>(
            inflater,
            R.layout.new_workingschedule_fragment,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = newWorkingScheduleViewModel
        return binding.root
    }

}
