package it.uniroma1.keeptime.data.model

import android.icu.util.Currency
import android.net.Uri
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError

import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest
import it.uniroma1.keeptime.data.CurrencySerializer
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Class for workers.
 */
@Serializable
open class Worker(
    var billRate: Double,
    var currency: Currency,
    email: String,
    url: Uri,
    val workspaces: List<WorkspaceReference>
) :
    WorkerReference(email, url) {

    @Serializer(forClass = Worker::class)
    companion object : KSerializer<Worker> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Worker") {
            element<Int>("bill_rate_cents")
            element("currency", CurrencySerializer.descriptor)
            element<String>("email")
            element("url", UriSerializer.descriptor)
            element<List<WorkspaceReference>>("workspaces")
        }

        override fun serialize(encoder: Encoder, value: Worker) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeIntElement(descriptor, 0, (value.billRate * 100).toInt())
            compositeOutput.encodeSerializableElement(descriptor, 1, CurrencySerializer, value.currency)
            compositeOutput.encodeStringElement(descriptor, 2, value.email)
            compositeOutput.encodeSerializableElement(descriptor, 3, UriSerializer, value.url)
            compositeOutput.encodeSerializableElement(
                descriptor,
                4,
                ListSerializer(WorkspaceReference.serializer()),
                value.workspaces
            )
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Worker {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var billRate: Double? = null
            var currency: Currency? = null
            var email: String? = null
            var url: Uri? = null
            var workspaces: List<WorkspaceReference>? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> billRate = dec.decodeIntElement(descriptor, 0) / 100.0
                    1 -> currency = dec.decodeSerializableElement(descriptor, 1, CurrencySerializer)
                    2 -> email = dec.decodeStringElement(descriptor, 2)
                    3 -> url = dec.decodeSerializableElement(descriptor, 3, UriSerializer)
                    4 -> workspaces =
                        dec.decodeSerializableElement(
                            descriptor, 4, ListSerializer(WorkspaceReference.serializer())
                        )
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return Worker(
                billRate ?: throw MissingFieldException("bill_rate_cents"),
                currency ?: throw MissingFieldException("currency"),
                email ?: throw MissingFieldException("email"),
                url ?: throw MissingFieldException("url"),
                workspaces ?: throw MissingFieldException("workspaces")
            )
        }

        /**
         * Retrieves a worker from the server.
         */
        fun fromServer(url: String, successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
            val loginRequest = AuthenticatedJsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response -> successCallback(Json.parse(serializer(), response.toString())) },
                Response.ErrorListener { error -> failCallback(error) })

            KeepTime.instance!!.requestQueue.add(loginRequest)
        }

        /**
         * Retrieves a worker from the server.
         */
        fun fromServer(url: Uri, successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
            fromServer(url.toString(), successCallback, failCallback)
        }
    }
}
