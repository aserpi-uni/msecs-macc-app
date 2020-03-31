package it.uniroma1.keeptime.ui.workspace

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.json.Json

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.WorkspaceReference
import it.uniroma1.keeptime.databinding.WorkspaceBinding
import it.uniroma1.keeptime.ui.base.BaseFragment
import it.uniroma1.keeptime.ui.modals.ModalBottomSheet

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

        workspaceViewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        })

        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navView: BottomNavigationView = view.findViewById(R.id.workspace_bottom_nav)
        val navController = Navigation.findNavController(view.findViewById(R.id.workspace_host_fragment))
        navView.setupWithNavController(navController)

        workspaceViewModel.getWorkspace(Json.parse(WorkspaceReference.serializer(), args.workspaceJson))
    }
}
