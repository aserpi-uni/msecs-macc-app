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
    val master: Boolean,
    name: String,
    url: Uri,
    val clients: List<ClientReference>,
    val projects: List<ProjectReference>,
    val workers: List<WorkerReference>
) : WorkspaceReference(name, url) {

    @Serializer(forClass = Workspace::class)
    companion object : INetwork<Workspace> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Workspace") {
            element<String>("description")
            element<Boolean>("master")
            element<String>("name")
            element("url", UriSerializer.descriptor)
            element<List<ClientReference>>("clients")
            element<List<ProjectReference>>("projects")
            element<List<WorkerReference>>("workers")
        }

        override fun serialize(encoder: Encoder, value: Workspace) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeStringElement(descriptor, 0, value.description)
            compositeOutput.encodeBooleanElement(descriptor, 1, value.master)
            compositeOutput.encodeStringElement(descriptor, 2, value.name)
            compositeOutput.encodeSerializableElement(descriptor, 3, UriSerializer, value.url)
            compositeOutput.encodeSerializableElement(
                descriptor,
                4,
                ListSerializer(ClientReference.serializer()),
                value.clients
            )
            compositeOutput.encodeSerializableElement(
                descriptor,
                5,
                ListSerializer(ProjectReference.serializer()),
                value.projects
            )
            compositeOutput.encodeSerializableElement(
                descriptor,
                6,
                ListSerializer(WorkerReference.serializer()),
                value.workers
            )
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Workspace {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var description: String? = null
            var master: Boolean? = null
            var name: String? = null
            var url: Uri? = null
            var clients: List<ClientReference>? = null
            var projects: List<ProjectReference>? = null
            var workers: List<WorkerReference>? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> description = dec.decodeStringElement(descriptor, 0)
                    1 -> master = dec.decodeBooleanElement(descriptor, 1)
                    2 -> name = dec.decodeStringElement(descriptor, 2)
                    3 -> url = dec.decodeSerializableElement(descriptor, 3, UriSerializer)
                    4 -> clients = dec.decodeSerializableElement(
                        descriptor, 4, ListSerializer(ClientReference.serializer())
                    )
                    5 -> projects = dec.decodeSerializableElement(
                        descriptor, 5, ListSerializer(ProjectReference.serializer())
                    )
                    6 -> workers = dec.decodeSerializableElement(
                            descriptor, 6, ListSerializer(WorkerReference.serializer())
                    )
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return Workspace(
                description ?: throw MissingFieldException("description"),
                master ?: throw MissingFieldException("master"),
                name ?: throw MissingFieldException("name"),
                url ?: throw MissingFieldException("url"),
                clients ?: throw MissingFieldException("clients"),
                projects ?: throw MissingFieldException("projects"),
                workers ?: throw MissingFieldException("workers")
            )
        }
    }
}
