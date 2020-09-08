package it.uniroma1.keeptime.ui.subactivity

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.subactivity.*
import kotlinx.serialization.json.Json

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.SubactivityReference
import it.uniroma1.keeptime.data.model.Subactivity
import it.uniroma1.keeptime.data.model.WorkingscheduleReference
import it.uniroma1.keeptime.databinding.SubactivityBinding
import it.uniroma1.keeptime.ui.base.BaseFragment
import it.uniroma1.keeptime.ui.modals.ModalBottomSheet
import kotlinx.android.synthetic.main.subactivity.view.*


class SubactivityFragment : BaseFragment() {
    private val args: SubactivityFragmentArgs by navArgs()
    private lateinit var subactivityReference: SubactivityReference
    private lateinit var subactivityViewModel: SubactivityViewModel

    private lateinit var workingschedulesAdapter: WorkingschedulesAdapter
    private lateinit var workingschedulesLayoutManager: LinearLayoutManager

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val infoItem = menu.findItem(R.id.action_info)
        infoItem.isVisible = true
        infoItem.setOnMenuItemClickListener {
            if(subactivityViewModel.subactivity.value == null) return@setOnMenuItemClickListener true
            ModalBottomSheet(
                subactivityViewModel.subactivity.value!!.description,
                subactivityViewModel.subactivity.value!!.status,
                subactivityViewModel.subactivity.value!!.deliveryTime
            ).show(parentFragmentManager, "info")
            true
        }

        val refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem.isVisible = true
        refreshItem.setOnMenuItemClickListener {
            if(! ::subactivityReference.isInitialized) return@setOnMenuItemClickListener true
            subactivityViewModel.refreshSubactivity(subactivityReference)
            true
        }

        val finishItem = menu.findItem(R.id.action_finish)
        subactivityViewModel.subactivity.observe(viewLifecycleOwner, Observer {
            val subactivity = it ?: return@Observer
            if(subactivity.status == "finished") {
                finishItem.isVisible = false
            } else {
                finishItem.isVisible = true
                finishItem.setOnMenuItemClickListener {
                    subactivityViewModel.finish(subactivityReference)
                    true
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        subactivityReference = Json.parse(SubactivityReference.serializer(), args.subactivityJson)

        workingschedulesAdapter =
            WorkingschedulesAdapter(
                emptyList(),
                ::onWorkingscheduleClicked
            )
        workingschedulesLayoutManager = LinearLayoutManager(context)
        subactivityViewModel = ViewModelProvider(this).get(SubactivityViewModel::class.java)
        viewModel = subactivityViewModel

        val binding = DataBindingUtil.inflate<SubactivityBinding>(
            inflater,
            R.layout.subactivity,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = subactivityViewModel

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.new_workingschedule_button.setOnClickListener(::onNewWorkingscheduleClicked)

        workingschedulesRecycler.apply {
            adapter = workingschedulesAdapter
            layoutManager = workingschedulesLayoutManager
            addItemDecoration((DividerItemDecoration(context, workingschedulesLayoutManager.orientation)))
        }

        workingschedulesSwipe.setOnRefreshListener {
            subactivityViewModel.refreshSubactivity(subactivityReference)
        }

        subactivityViewModel.subactivity.observe(viewLifecycleOwner, Observer {
            if(it !is Subactivity) return@Observer

            workingschedulesAdapter.replace(it.workingschedules)
            view.workingschedulesSwipe.isRefreshing = false
        })

        subactivityViewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            view.workingschedulesSwipe.isRefreshing = false
        })

        subactivityViewModel.refreshSubactivity(subactivityReference)
    }

    private fun onWorkingscheduleClicked(workingschedule: WorkingscheduleReference) {
        /*
        val action = ProjectFragmentDirections.actionToWorkspace(
            Subactivity.description, Json.stringify(SubactivityReference.serializer(), Subactivity)
        )
        findNavController().navigate(action)
         */
    }
    private fun onNewWorkingscheduleClicked(view: View){
        val action = SubactivityFragmentDirections.actionToNewWorkingSchedule(
            subactivityViewModel.subactivity.value!!.url.toString()
        )
        findNavController().navigate(action)
    }
}
