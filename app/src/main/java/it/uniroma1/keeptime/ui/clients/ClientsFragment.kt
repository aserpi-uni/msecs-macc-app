package it.uniroma1.keeptime.ui.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import it.uniroma1.keeptime.R

class ClientsFragment : Fragment() {

    private lateinit var clientsViewModel: ClientsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clientsViewModel =
            ViewModelProviders.of(this).get(ClientsViewModel::class.java)
        val root = inflater.inflate(R.layout.clients, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        clientsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}
