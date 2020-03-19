package it.uniroma1.keeptime.data.model

import android.net.Uri
import com.android.volley.VolleyError

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */

// TODO: secure token
class LoggedInUser(val authenticationToken: String, email: String, url: String) :
    Worker(0, "eur", email, url) {

    fun getFromServer(url: Uri, successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
        Worker.getFromServer(url, returnLoggedInUser(successCallback), failCallback)
    }

    private fun returnLoggedInUser(successCallback: (Worker) -> Any) : (Worker) -> Any {
        return { worker ->
            bill_rate_cents = worker.bill_rate_cents
            currency = worker.currency
            successCallback(worker)
        }
    }
}
