package it.uniroma1.keeptime.ui.workspaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import it.uniroma1.keeptime.R

class WorkspacesFragment : Fragment() {

    private lateinit var workspacesViewModel: WorkspacesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        workspacesViewModel =
            ViewModelProviders.of(this).get(WorkspacesViewModel::class.java)
        val root = inflater.inflate(R.layout.workspaces, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        workspacesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}
