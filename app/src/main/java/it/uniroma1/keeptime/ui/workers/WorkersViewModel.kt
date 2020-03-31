package it.uniroma1.keeptime.ui.workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorkersViewModel : ViewModel() {

    private val _text = MutableLiveData("This is workers Fragment")
    val text: LiveData<String> = _text
}
