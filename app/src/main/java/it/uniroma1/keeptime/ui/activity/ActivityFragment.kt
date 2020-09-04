package it.uniroma1.keeptime.ui.activity

import android.os.Bundle
import android.text.TextUtils.replace
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity.*
import kotlinx.serialization.json.Json

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.SubactivityReference
import it.uniroma1.keeptime.data.model.Activity
import it.uniroma1.keeptime.data.model.ActivityReference
import it.uniroma1.keeptime.databinding.ActivityBinding
import it.uniroma1.keeptime.ui.base.BaseFragment
import it.uniroma1.keeptime.ui.modals.ModalBottomSheet




class ActivityFragment : BaseFragment() {
    private val args: ActivityFragmentArgs by navArgs()
    private lateinit var activityReference: ActivityReference

    private lateinit var subactivitiesAdapter: SubactivitiesAdapter
    private lateinit var subactivitiesLayoutManager: LinearLayoutManager
    private lateinit var activityViewModel: ActivityViewModel

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val infoItem = menu.findItem(R.id.action_info)
        infoItem.isVisible = true
        infoItem.setOnMenuItemClickListener {
            if(activityViewModel.activity.value == null) return@setOnMenuItemClickListener true
            ModalBottomSheet(
                activityViewModel.activity.value!!.description,
                activityViewModel.activity.value!!.status,
                activityViewModel.activity.value!!.deliveryTime
            ).show(parentFragmentManager, "info")
            true
        }

        val refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem.isVisible = true
        refreshItem.setOnMenuItemClickListener {
            if(! ::activityReference.isInitialized) return@setOnMenuItemClickListener true
            activityViewModel.refreshActivity(activityReference)
            true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activityReference = Json.parse(ActivityReference.serializer(), args.activityJson)

        subactivitiesAdapter = SubactivitiesAdapter(emptyList(), ::onSubactivityClicked)
        subactivitiesLayoutManager = LinearLayoutManager(context)
        activityViewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)
        viewModel = activityViewModel

        val binding = DataBindingUtil.inflate<ActivityBinding>(
            inflater,
            R.layout.activity,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = activityViewModel

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subactivitiesRecycler.apply {
            adapter = subactivitiesAdapter
            layoutManager = subactivitiesLayoutManager
            addItemDecoration((DividerItemDecoration(context, subactivitiesLayoutManager.orientation)))
        }

        subactivitiesSwipe.setOnRefreshListener {
            activityViewModel.refreshActivity(activityReference)
        }

        activityViewModel.activity.observe(viewLifecycleOwner, Observer {
            if(it !is Activity) return@Observer

            subactivitiesAdapter.replace(it.subactivities)
            subactivitiesSwipe.isRefreshing = false
        })

        activityViewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            subactivitiesSwipe.isRefreshing = false
        })

        activityViewModel.refreshActivity(activityReference)
    }

    private fun onSubactivityClicked(Subactivity: SubactivityReference) {
        /*
        val action = ProjectFragmentDirections.actionToWorkspace(
            Subactivity.description, Json.stringify(SubactivityReference.serializer(), Subactivity)
        )
        findNavController().navigate(action)
         */
    }
}
