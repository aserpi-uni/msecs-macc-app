package it.uniroma1.keeptime.ui.workspaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.workspaces.*

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker


class WorkspacesFragment : Fragment() {

    private lateinit var workspacesViewModel: WorkspacesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
        val root = inflater.inflate(R.layout.workspaces, container, false)

        val listLayoutManager = LinearLayoutManager(context)
        LoginRepository.user.observe(viewLifecycleOwner, Observer {
            if(it !is Worker || it.workspaces.isEmpty()) return@Observer
            val workspaces = it.workspaces

            workspacesRecycler.apply {
                adapter = WorkspacesAdapter(workspaces)
                layoutManager = listLayoutManager
                addItemDecoration(DividerItemDecoration(context, listLayoutManager.orientation))
            }
        })

        return root
    }
}
