package it.uniroma1.keeptime.data.model

import android.net.Uri
import com.android.volley.VolleyError
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.*

/**
 * Base class for workers. It contains only the worker's url and their email address.
 */
@Serializable
open class WorkerReference(var email: String, @Serializable(with = UriSerializer::class) val url: Uri) {
    constructor(email: String, url: String) : this(email, Uri.parse(url))

    /**
     * Retrieves the worker's attributes from the server.
     */
    fun fromServer(successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
        Worker.fromServer(url, successCallback, failCallback)
    }
}
