package it.uniroma1.keeptime.data.model

import android.net.Uri
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer

import it.uniroma1.keeptime.data.ColorSerializer
import it.uniroma1.keeptime.data.UriSerializer

/**
 * Class for clients.
 */
@Serializable
class Client(
    color: Int?,
    val description: String,
    name: String,
    url: Uri,
    val workspaces: List<WorkspaceReference>
) : ClientReference(color, name, url) {

    @Serializer(forClass = Client::class)
    companion object : INetwork<Client> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Client") {
            element<Int>("color")
            element<String>("description")
            element<String>("name")
            element("url", UriSerializer.descriptor)
            element<List<WorkspaceReference>>("workspaces")
        }

        override fun serialize(encoder: Encoder, value: Client) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeSerializableElement(descriptor, 0, ColorSerializer, value.color)
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
            var color: Int? = null
            var description: String? = null
            var name: String? = null
            var url: Uri? = null
            var workspaces: List<WorkspaceReference>? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> color = dec.decodeSerializableElement(descriptor, 0, ColorSerializer)
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
    }
}
