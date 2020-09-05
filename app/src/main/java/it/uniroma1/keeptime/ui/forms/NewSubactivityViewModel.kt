package it.uniroma1.keeptime.ui.forms
import android.icu.util.Currency
import android.util.Patterns
import android.view.View
import androidx.lifecycle.*
import com.android.volley.*
import kotlinx.coroutines.launch
import org.json.JSONObject

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.ui.base.BaseViewModel

class NewSubactivityViewModel : BaseViewModel {
    val description: MutableLiveData<String>()

}