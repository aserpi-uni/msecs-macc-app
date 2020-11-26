package it.uniroma1.keeptime.ui.forms

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.DateSerializer
import it.uniroma1.keeptime.databinding.NewWorkingscheduleFragmentBinding

import it.uniroma1.keeptime.ui.base.BaseFragment
import kotlinx.android.synthetic.main.new_workingschedule_fragment.*
import kotlinx.serialization.json.Json
import java.util.*

class NewWorkingScheduleFragment : BaseFragment() {
    private val args: NewWorkingScheduleFragmentArgs by navArgs()

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
    override fun onViewCreated(view:View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        //var date_text = view.findViewById(R.id.viewDate) as TextView
        newWorkingScheduleViewModel._subactivityUrl.value = args.subactivityUrl
        var date: Date
        prompt_working_date.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val today = MaterialDatePicker.todayInUtcMilliseconds()
            builder.setSelection(today)
            val picker = builder.build()
            picker.addOnPositiveButtonClickListener { selection ->
                date = Date(selection)
                newWorkingScheduleViewModel._date.value = date
            }
            picker.show(childFragmentManager, picker.toString())
        }
    }

}
