package it.uniroma1.keeptime.ui.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.clients.*

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.ClientReference
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.data.model.Workspace
import it.uniroma1.keeptime.data.model.WorkspaceReference
import it.uniroma1.keeptime.databinding.ClientsBinding
import it.uniroma1.keeptime.ui.base.BaseFragment
import it.uniroma1.keeptime.ui.workspace.WorkspaceFragment


class ClientsFragment : BaseFragment() {
    private var workspaceReference: WorkspaceReference? = null

    private lateinit var clientsViewModel: ClientsViewModel
    private lateinit var clientsAdapter: ClientsAdapter
    private lateinit var clientsLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        clientsAdapter = ClientsAdapter((LoginRepository.user.value as Worker).clients, ::onClientClicked)
        clientsLayoutManager = LinearLayoutManager(context)
        clientsViewModel = ViewModelProvider(this).get(ClientsViewModel::class.java)
        viewModel = clientsViewModel

        val binding = DataBindingUtil.inflate<ClientsBinding>(
            inflater,
            R.layout.clients,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = clientsViewModel

        workspaceReference = (parentFragment?.parentFragment as? WorkspaceFragment)?.workspaceReference
        clientsViewModel.refreshClients(workspaceReference)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clientsRecycler.apply {
            adapter = clientsAdapter
            layoutManager = clientsLayoutManager
            addItemDecoration((DividerItemDecoration(context, clientsLayoutManager.orientation)))
        }

        clientsSwipe.setOnRefreshListener {
            clientsViewModel.refreshClients(workspaceReference)
        }

        if (workspaceReference != null) {
            clientsViewModel.workspace.observe(viewLifecycleOwner, Observer {
                if(it !is Workspace) return@Observer

                clientsAdapter.replace(it.clients)
                clientsSwipe.isRefreshing = false
            })
        } else {
            LoginRepository.user.observe(viewLifecycleOwner, Observer {
                if(it !is Worker) return@Observer

                clientsAdapter.replace(it.clients)
                clientsSwipe.isRefreshing = false
            })
        }

        clientsViewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            clientsSwipe.isRefreshing = false
        })
    }

    private fun onClientClicked(client: ClientReference) {
        // TODO: navigate to client page
    }
}
