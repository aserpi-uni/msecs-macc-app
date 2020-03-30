package it.uniroma1.keeptime.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import it.uniroma1.keeptime.R

class LogFragment : Fragment() {

    private lateinit var logViewModel: LogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logViewModel =
            ViewModelProviders.of(this).get(LogViewModel::class.java)
        val root = inflater.inflate(R.layout.log, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        logViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}
