package it.uniroma1.keeptime.ui.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.projects.*
import kotlinx.serialization.json.Json

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.data.model.ProjectReference
import it.uniroma1.keeptime.databinding.ProjectsBinding
import it.uniroma1.keeptime.ui.base.BaseFragment


class ProjectsFragment : BaseFragment() {

    private lateinit var projectsViewModel: ProjectsViewModel
    private lateinit var projectsAdapter: ProjectsAdapter
    private lateinit var projectsLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        projectsAdapter = ProjectsAdapter((LoginRepository.user.value as Worker).projects, ::onProjectClicked)
        projectsLayoutManager = LinearLayoutManager(context)
        projectsViewModel = ViewModelProvider(this).get(ProjectsViewModel::class.java)
        viewModel = projectsViewModel

        val binding = DataBindingUtil.inflate<ProjectsBinding>(
            inflater,
            R.layout.projects,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = projectsViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        projectsRecycler.apply {
            adapter = projectsAdapter
            layoutManager = projectsLayoutManager
            addItemDecoration((DividerItemDecoration(context, projectsLayoutManager.orientation)))
        }

        projectsSwipe.setOnRefreshListener {
            projectsViewModel.refreshProjects()
        }

        LoginRepository.user.observe(viewLifecycleOwner, Observer {
            if(it !is Worker) return@Observer

            projectsAdapter.replace(it.projects)
            projectsSwipe.isRefreshing = false
        })

        projectsViewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            projectsSwipe.isRefreshing = false
        })
    }

    private fun onProjectClicked(project: ProjectReference) {
        val action = ProjectsFragmentDirections.actionToProject(
            project.projectName, Json.stringify(ProjectReference.serializer(), project)
        )
        findNavController().navigate(action)
    }
}
