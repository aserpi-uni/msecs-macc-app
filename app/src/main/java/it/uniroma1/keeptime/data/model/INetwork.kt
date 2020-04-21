package it.uniroma1.keeptime.data.model

import android.net.Uri
import com.android.volley.Request
import com.android.volley.Response
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlinx.serialization.json.Json
import kotlin.coroutines.resumeWithException

import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest


/**
 * Interface for retrieving objects from the server.
 */
interface INetwork<T> : KSerializer<T>

/**
 * Retrieves a resource from the server.
 */
suspend inline fun <reified T> INetwork<T>.fromServer(url: String): T = suspendCancellableCoroutine { cont ->
    val request = AuthenticatedJsonObjectRequest(
        Request.Method.GET, url, null,
        Response.Listener { cont.resume(Json.parse(serializer(), it.toString())) { } },
        Response.ErrorListener { cont.resumeWithException(it) }
    )

    KeepTime.instance.requestQueue.add(request)

    cont.invokeOnCancellation {
        request.cancel()
    }
}

/**
 * Retrieves a resource from the server.
 */
suspend inline fun <reified T> INetwork<T>.fromServer(url: Uri): T = fromServer(url.toString())
