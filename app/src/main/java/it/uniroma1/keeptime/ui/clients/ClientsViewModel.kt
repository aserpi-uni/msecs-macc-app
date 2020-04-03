package it.uniroma1.keeptime.ui.clients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import it.uniroma1.keeptime.ui.base.BaseViewModel


class ClientsViewModel : BaseViewModel() {

    private val _text = MutableLiveData("This is tools Fragment")
    val text: LiveData<String> = _text
}
