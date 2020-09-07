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
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.DateSerializer
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.data.model.WorkerReference
import it.uniroma1.keeptime.databinding.NewSubactivityBinding
import it.uniroma1.keeptime.ui.activity.ActivityFragmentArgs
import it.uniroma1.keeptime.ui.base.BaseFragment
import kotlinx.android.synthetic.main.new_subactivity.view.*
import kotlinx.android.synthetic.main.user_preferences_fragment.view.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import java.util.*


class NewSubactivityFragment : BaseFragment(){
    private val args: NewSubactivityFragmentArgs by navArgs()
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
        newSubactivityViewModel.baseUrl = args.activityUrl

        val binding = DataBindingUtil.inflate<NewSubactivityBinding>(
            inflater,
            R.layout.new_subactivity,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = newSubactivityViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var date_text = view.findViewById(R.id.viewDate) as TextView
        var date_button  = view.findViewById(R.id.delivery_date_button) as Button
        var date:Date


        date_button.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val today = MaterialDatePicker.todayInUtcMilliseconds()
            builder.setSelection(today)
            val picker = builder.build()
            picker.addOnPositiveButtonClickListener { selection ->
                date = Date(selection)
                date_text.text = Json.stringify(DateSerializer, date)
                newSubactivityViewModel._deliveryDate.value = date
            }
            picker.show(childFragmentManager, picker.toString())
        }

        val url = args.workspaceUrl.dropLast(5) + "/show_worker_ids.json"

        newSubactivityViewModel.workers.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it !is List<WorkerReference>) return@Observer
            val allWorkers = it

            var selectedIdx_1 = 0
            view.prompt_worker_1.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.select_worker_1)
                    .setSingleChoiceItems(allWorkers.map { it.email }.toTypedArray(), selectedIdx_1) { _, idx ->
                        selectedIdx_1 = idx
                    }
                    .setPositiveButton(R.string.save) { _, _ -> newSubactivityViewModel.setWorker1(allWorkers[selectedIdx_1]) }
                    .setNegativeButton(R.string.cancel, null).show()
            }
            
            var selectedIdx_2 = 0
            view.prompt_worker_2.setOnClickListener{
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.select_worker_2)
                    .setSingleChoiceItems(allWorkers.map{it.email}.toTypedArray(), selectedIdx_2){
                            _, idx -> selectedIdx_2 = idx
                    }
                    .setPositiveButton(R.string.save){_, _ -> newSubactivityViewModel.setWorker2(allWorkers[selectedIdx_2])}
                    .setNegativeButton(R.string.cancel, null).show()
            }
            
            var selectedIdx_3 = 0
            view.prompt_worker_3.setOnClickListener{
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.select_worker_3)
                    .setSingleChoiceItems(allWorkers.map{it.email}.toTypedArray(), selectedIdx_3){
                            _, idx -> selectedIdx_3 = idx
                    }
                    .setPositiveButton(R.string.save){_, _ -> newSubactivityViewModel.setWorker3(allWorkers[selectedIdx_3])}
                    .setNegativeButton(R.string.cancel, null).show()
            }
        })

        newSubactivityViewModel.getWorkerIds(url)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menu.findItem(R.id.action_settings).isVisible = false
    }
}
