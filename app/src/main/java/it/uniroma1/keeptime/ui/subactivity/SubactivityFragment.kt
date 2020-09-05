package it.uniroma1.keeptime.ui.subactivity

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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


class SubactivityFragment : BaseFragment() {
    private val args: SubactivityFragmentArgs by navArgs()
    private lateinit var subactivityReference: SubactivityReference

    private lateinit var workingschedulesAdapter: WorkingschedulesAdapter
    private lateinit var workingschedulesLayoutManager: LinearLayoutManager
    private lateinit var subactivityViewModel: SubactivityViewModel
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val infoItem = menu.findItem(R.id.action_info)
        infoItem.isVisible = true
        infoItem.setOnMenuItemClickListener {
            if(subactivityViewModel.activity.value == null) return@setOnMenuItemClickListener true
            ModalBottomSheet(
                subactivityViewModel.activity.value!!.description,
                subactivityViewModel.activity.value!!.status,
                subactivityViewModel.activity.value!!.deliveryTime
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

        workingschedulesRecycler.apply {
            adapter = workingschedulesAdapter
            layoutManager = workingschedulesLayoutManager
            addItemDecoration((DividerItemDecoration(context, workingschedulesLayoutManager.orientation)))
        }

        workingschedulesSwipe.setOnRefreshListener {
            subactivityViewModel.refreshSubactivity(subactivityReference)
        }

        subactivityViewModel.activity.observe(viewLifecycleOwner, Observer {
            if(it !is Subactivity) return@Observer

            workingschedulesAdapter.replace(it.workingschedules)
            workingschedulesSwipe.isRefreshing = false
        })

        subactivityViewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            workingschedulesSwipe.isRefreshing = false
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
}
