package it.uniroma1.keeptime.data.model

import android.net.Uri
import com.android.volley.Request
import com.android.volley.Response
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.coroutines.resumeWithException

import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest
import it.uniroma1.keeptime.data.UriSerializer

/**
 * Class for clients.
 */
@Serializable
class Client(
    val color: Long,
    val description: String,
    name: String,
    url: Uri,
    val workspaces: List<WorkspaceReference>
) : ClientReference(name, url) {

    @Serializer(forClass = Client::class)
    companion object : KSerializer<Client> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Client") {
            element<Long>("color")
            element<String>("description")
            element<String>("name")
            element("url", UriSerializer.descriptor)
            element<List<WorkspaceReference>>("workspaces")
        }

        override fun serialize(encoder: Encoder, value: Client) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeLongElement(descriptor, 0, value.color)
            compositeOutput.encodeStringElement(descriptor, 1, value.description)
            compositeOutput.encodeStringElement(descriptor, 2, value.name)
            compositeOutput.encodeSerializableElement(descriptor, 3, UriSerializer, value.url)
            compositeOutput.encodeSerializableElement(
                descriptor,
                4,
                ListSerializer(WorkspaceReference.serializer()),
                value.workspaces
            )
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Client {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var color: Long? = null
            var description: String? = null
            var name: String? = null
            var url: Uri? = null
            var workspaces: List<WorkspaceReference>? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> color = dec.decodeLongElement(descriptor, 0)
                    1 -> description = dec.decodeStringElement(descriptor, 1)
                    2 -> name = dec.decodeStringElement(descriptor, 2)
                    3 -> url = dec.decodeSerializableElement(descriptor, 3, UriSerializer)
                    4 -> workspaces =
                        dec.decodeSerializableElement(
                            descriptor, 4, ListSerializer(WorkspaceReference.serializer())
                        )
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return Client(
                color ?: throw MissingFieldException("color"),
                description ?: throw MissingFieldException("description"),
                name ?: throw MissingFieldException("name"),
                url ?: throw MissingFieldException("url"),
                workspaces ?: throw MissingFieldException("workspaces")
            )
        }

        /**
         * Retrieves a client from the server.
         */
        suspend fun fromServer(url: String): Client = suspendCancellableCoroutine { cont ->
            val request = AuthenticatedJsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { cont.resume(Json.parse(serializer(), it.toString())) { } },
                Response.ErrorListener { cont.resumeWithException(it) })

            KeepTime.instance.requestQueue.add(request)

            cont.invokeOnCancellation {
                request.cancel()
            }
        }

        /**
         * Retrieves a client from the server.
         */
        suspend fun fromServer(url: Uri): Client = fromServer(url.toString())
    }
}
