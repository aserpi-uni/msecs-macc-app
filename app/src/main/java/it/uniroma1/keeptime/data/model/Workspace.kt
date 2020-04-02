package it.uniroma1.keeptime.data.model

import android.net.Uri
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer

import it.uniroma1.keeptime.data.UriSerializer


/**
 * Class for workers.
 */
@Serializable
open class Workspace(
    val description: String,
    name: String,
    url: Uri,
    val workers: List<WorkerReference>
) : WorkspaceReference(name, url) {

    @Serializer(forClass = Workspace::class)
    companion object : INetwork<Workspace> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Workspace") {
            element<String>("description")
            element<String>("name")
            element("url", UriSerializer.descriptor)
            element<List<WorkerReference>>("workers")
        }

        override fun serialize(encoder: Encoder, value: Workspace) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeStringElement(descriptor, 0, value.description)
            compositeOutput.encodeStringElement(descriptor, 1, value.name)
            compositeOutput.encodeSerializableElement(descriptor, 2, UriSerializer, value.url)
            compositeOutput.encodeSerializableElement(
                descriptor,
                3,
                ListSerializer(WorkerReference.serializer()),
                value.workers
            )
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Workspace {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var description: String? = null
            var name: String? = null
            var url: Uri? = null
            var workers: List<WorkerReference>? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> description = dec.decodeStringElement(descriptor, 0)
                    1 -> name = dec.decodeStringElement(descriptor, 1)
                    2 -> url = dec.decodeSerializableElement(descriptor, 2, UriSerializer)
                    3 -> workers = dec.decodeSerializableElement(
                            descriptor, 3, ListSerializer(WorkerReference.serializer())
                    )
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return Workspace(
                description ?: throw MissingFieldException("description"),
                name ?: throw MissingFieldException("name"),
                url ?: throw MissingFieldException("url"),
                workers ?: throw MissingFieldException("workers")
            )
        }
    }
}
