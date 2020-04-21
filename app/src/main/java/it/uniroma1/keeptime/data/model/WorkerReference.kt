package it.uniroma1.keeptime.data.model

import android.net.Uri
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
    suspend fun fromServer(): Worker = Worker.fromServer(url)
}
