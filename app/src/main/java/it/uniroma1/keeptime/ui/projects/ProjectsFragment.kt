package it.uniroma1.keeptime.ui.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import it.uniroma1.keeptime.R

class ProjectsFragment : Fragment() {

    private lateinit var projectsViewModel: ProjectsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        projectsViewModel =
            ViewModelProviders.of(this).get(ProjectsViewModel::class.java)
        val root = inflater.inflate(R.layout.projects, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        projectsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}
