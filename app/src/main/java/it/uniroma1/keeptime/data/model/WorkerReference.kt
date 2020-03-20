package it.uniroma1.keeptime.data.model

import android.net.Uri
import com.android.volley.VolleyError

open class WorkerReference(var email: String, url_: String) {
    val url: Uri = Uri.parse(url_)

    fun getFromServer(successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
        Worker.getFromServer(url, successCallback, failCallback)
    }
}
