package it.uniroma1.keeptime.ui.workspace

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.serialization.json.Json

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.WorkspaceReference
import it.uniroma1.keeptime.databinding.WorkspaceBinding
import it.uniroma1.keeptime.ui.base.BaseFragment
import it.uniroma1.keeptime.ui.modals.ModalBottomSheet

class WorkspaceFragment : BaseFragment() {
    private val args: WorkspaceFragmentArgs by navArgs()
    lateinit var workspaceReference: WorkspaceReference
    lateinit var workspaceViewModel: WorkspaceViewModel

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val infoItem = menu.findItem(R.id.action_info)
        infoItem.isVisible = true
        infoItem.setOnMenuItemClickListener {
            if(workspaceViewModel.workspace.value == null) return@setOnMenuItemClickListener true
            ModalBottomSheet(workspaceViewModel.workspace.value!!.description).show(parentFragmentManager, "info")
            true
        }

        val refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem.isVisible = true
        refreshItem.setOnMenuItemClickListener {
            if(! ::workspaceReference.isInitialized) return@setOnMenuItemClickListener true
            workspaceViewModel.getWorkspace(workspaceReference)
            true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        workspaceViewModel = ViewModelProvider(this).get(WorkspaceViewModel::class.java)
        viewModel = workspaceViewModel

        val binding = DataBindingUtil.inflate<WorkspaceBinding>(
            inflater,
            R.layout.workspace,
            container,
            false
        )
        val view = binding.root

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = workspaceViewModel

        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navView: BottomNavigationView = view.findViewById(R.id.workspace_bottom_nav)
        val navController = Navigation.findNavController(view.findViewById(R.id.workspace_host_fragment))
        navView.setupWithNavController(navController)

        workspaceReference = Json.parse(WorkspaceReference.serializer(), args.workspaceJson)
        workspaceViewModel.getWorkspace(workspaceReference)
    }
}
