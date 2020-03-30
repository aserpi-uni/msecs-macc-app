package it.uniroma1.keeptime.ui.workspaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorkspacesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is workspaces Fragment"
    }
    val text: LiveData<String> = _text
}
