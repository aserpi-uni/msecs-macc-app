package it.uniroma1.keeptime.ui.workspaces

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
import kotlinx.android.synthetic.main.workspaces.*
import kotlinx.serialization.json.Json

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.data.model.WorkspaceReference
import it.uniroma1.keeptime.databinding.WorkspacesBinding
import it.uniroma1.keeptime.ui.base.BaseFragment


class WorkspacesFragment : BaseFragment() {

    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var workspacesAdapter: WorkspacesAdapter
    private lateinit var workspacesLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        workspacesAdapter = WorkspacesAdapter((LoginRepository.user.value as Worker).workspaces, ::onWorkspaceClicked)
        workspacesLayoutManager = LinearLayoutManager(context)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
        viewModel = workspacesViewModel

        val binding = DataBindingUtil.inflate<WorkspacesBinding>(
            inflater,
            R.layout.workspaces,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = workspacesViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workspacesRecycler.apply {
            adapter = workspacesAdapter
            layoutManager = workspacesLayoutManager
            addItemDecoration((DividerItemDecoration(context, workspacesLayoutManager.orientation)))
        }

        workspacesSwipe.setOnRefreshListener {
            workspacesViewModel.refreshWorkspaces()
        }

        LoginRepository.user.observe(viewLifecycleOwner, Observer {
            if(it !is Worker) return@Observer

            workspacesAdapter.replace(it.workspaces)
            workspacesSwipe.isRefreshing = false
        })

        workspacesViewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            workspacesSwipe.isRefreshing = false
        })
    }

    private fun onWorkspaceClicked(workspace: WorkspaceReference) {
        val action = WorkspacesFragmentDirections.actionToWorkspace(
            workspace.name, Json.stringify(WorkspaceReference.serializer(), workspace)
        )
        findNavController().navigate(action)
    }
}
