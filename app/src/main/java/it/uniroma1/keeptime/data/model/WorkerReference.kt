package it.uniroma1.keeptime.data.model

import android.net.Uri
import com.android.volley.VolleyError
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.*

@Serializable
open class WorkerReference(var email: String, @Serializable(with = UriSerializer::class) val url: Uri) {
    constructor(email: String, url: String) : this(email, Uri.parse(url))

    fun getFromServer(successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
        Worker.getFromServer(url, successCallback, failCallback)
    }
}
