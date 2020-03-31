package it.uniroma1.keeptime.ui.workers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import it.uniroma1.keeptime.R


class WorkersFragment : Fragment() {

    private lateinit var workersViewModel: WorkersViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        workersViewModel = ViewModelProvider(this).get(WorkersViewModel::class.java)
        val root = inflater.inflate(R.layout.clients, container, false)

        val textView: TextView = root.findViewById(R.id.text_workers)
        workersViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }
}
