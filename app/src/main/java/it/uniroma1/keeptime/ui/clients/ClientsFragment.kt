package it.uniroma1.keeptime.ui.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.databinding.ClientsBinding
import it.uniroma1.keeptime.ui.base.BaseFragment


class ClientsFragment : BaseFragment() {

    private lateinit var clientsViewModel: ClientsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        clientsViewModel = ViewModelProvider(this).get(ClientsViewModel::class.java)
        viewModel = clientsViewModel

        val binding = ClientsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = clientsViewModel

        val view = binding.root
        val textView: TextView = view.findViewById(R.id.text_tools)
        clientsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return view
    }
}
