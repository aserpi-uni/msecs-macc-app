package it.uniroma1.keeptime.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.ui.base.BaseFragment

class LogFragment : BaseFragment() {

    private lateinit var logViewModel: LogViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        logViewModel = ViewModelProvider(this).get(LogViewModel::class.java)
        viewModel = logViewModel

        return inflater.inflate(R.layout.log, container, false)
    }
}
