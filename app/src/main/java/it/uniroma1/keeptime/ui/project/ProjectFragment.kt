package it.uniroma1.keeptime.ui.project

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.project.*
import kotlinx.serialization.json.Json

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.Activity
import it.uniroma1.keeptime.data.model.Project
import it.uniroma1.keeptime.data.model.ProjectReference
import it.uniroma1.keeptime.databinding.ProjectBinding
import it.uniroma1.keeptime.ui.base.BaseFragment
import it.uniroma1.keeptime.ui.modals.ModalBottomSheet
import it.uniroma1.keeptime.ui.project.ProjectFragmentArgs


class ProjectFragment : BaseFragment() {
    private val args: ProjectFragmentArgs by navArgs()
    private lateinit var projectReference: ProjectReference

    private lateinit var activitiesAdapter: ActivitiesAdapter
    private lateinit var activitiesLayoutManager: LinearLayoutManager
    private lateinit var projectViewModel: ProjectViewModel

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val infoItem = menu.findItem(R.id.action_info)
        infoItem.isVisible = true
        infoItem.setOnMenuItemClickListener {
            if(projectViewModel.project.value == null) return@setOnMenuItemClickListener true
            ModalBottomSheet(projectViewModel.project.value!!.description).show(parentFragmentManager, "info")
            true
        }

        val refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem.isVisible = true
        refreshItem.setOnMenuItemClickListener {
            if(! ::projectReference.isInitialized) return@setOnMenuItemClickListener true
            projectViewModel.refreshProject(projectReference)
            true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        projectReference = Json.parse(ProjectReference.serializer(), args.projectJson)

        activitiesAdapter = ActivitiesAdapter(emptyList(), ::onActivityClicked)
        activitiesLayoutManager = LinearLayoutManager(context)
        projectViewModel = ViewModelProvider(this).get(ProjectViewModel::class.java)
        viewModel = projectViewModel

        val binding = DataBindingUtil.inflate<ProjectBinding>(
            inflater,
            R.layout.project,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = projectViewModel

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activitiesRecycler.apply {
            adapter = activitiesAdapter
            layoutManager = activitiesLayoutManager
            addItemDecoration((DividerItemDecoration(context, activitiesLayoutManager.orientation)))
        }

        activitiesSwipe.setOnRefreshListener {
            projectViewModel.refreshProject(projectReference)
        }

        projectViewModel.project.observe(viewLifecycleOwner, Observer {
            if(it !is Project) return@Observer

            activitiesAdapter.replace(it.activities)
            activitiesSwipe.isRefreshing = false
        })

        projectViewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            activitiesSwipe.isRefreshing = false
        })

        projectViewModel.refreshProject(projectReference)
    }

    private fun onActivityClicked(activity: Activity) {
        /*
        val action = ProjectFragmentDirections.actionToWorkspace(
            activity.description, Json.stringify(Activity.serializer(), activity)
        )
        findNavController().navigate(action)
         */
    }
}
