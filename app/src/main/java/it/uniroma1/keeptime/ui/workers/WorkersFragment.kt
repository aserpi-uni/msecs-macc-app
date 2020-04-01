package it.uniroma1.keeptime.ui.workers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.ui.workspace.WorkspaceFragment
import it.uniroma1.keeptime.ui.workspace.WorkspaceViewModel
import kotlinx.android.synthetic.main.workers.*


class WorkersFragment : Fragment() {

    private lateinit var workspaceViewModel: WorkspaceViewModel
    private lateinit var workersAdapter: WorkersAdapter
    private lateinit var workersLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        workspaceViewModel = ((parentFragment as NavHostFragment).parentFragment as WorkspaceFragment).workspaceViewModel
        workersAdapter = WorkersAdapter(workspaceViewModel.workspace.value!!.workers)
        workersLayoutManager = LinearLayoutManager(context)

        return inflater.inflate(R.layout.workers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workersRecycler.apply {
            adapter = workersAdapter
            layoutManager = workersLayoutManager
            addItemDecoration((DividerItemDecoration(context, workersLayoutManager.orientation)))
        }

        workspaceViewModel.workspace.observe(viewLifecycleOwner, Observer {
            val workspace = it ?: return@Observer
            workersAdapter.replace(workspace.workers)
        })
    }
}
