package it.uniroma1.keeptime.ui.workspaces

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import it.uniroma1.keeptime.LoginActivity
import kotlinx.android.synthetic.main.workspaces.*

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker


class WorkspacesFragment : Fragment() {

    private lateinit var workspacesAdapter: WorkspacesAdapter
    private lateinit var workspacesLayoutManager: LinearLayoutManager
    private lateinit var workspacesViewModel: WorkspacesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
        val root = inflater.inflate(R.layout.workspaces, container, false)

        workspacesAdapter = WorkspacesAdapter((LoginRepository.user.value as Worker).workspaces)
        workspacesLayoutManager = LinearLayoutManager(context)

        return root
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

        workspacesViewModel.logoutMessage.observe(viewLifecycleOwner, Observer {
            val logoutMessage = it ?: return@Observer

            LoginRepository.removeCredentials()
            val loginIntent = Intent(context, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            loginIntent.putExtra("message", R.string.failed_wrong_credentials)
            startActivity(loginIntent)

            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        })

        workspacesViewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer

            workspacesSwipe.isRefreshing = false
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        })
    }
}
