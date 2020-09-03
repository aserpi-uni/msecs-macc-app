package it.uniroma1.keeptime.data.model

import android.net.Uri
import it.uniroma1.keeptime.data.DateSerializer
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import java.util.*


/**
 * Class for projects.
 */
@Serializable
open class Project(
    val deliveryTime: Date,
    val description: String,
    projectName: String,
    val status: String,
    url: Uri,
    val activities: List<Activity>,
    val workspace: WorkspaceReference
) : ProjectReference(projectName, url) {

    @Serializer(forClass = Project::class)
    companion object : INetwork<Project> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Project") {
            element("deliveryTime", DateSerializer.descriptor)
            element<String>("description")
            element<String>("projectName")
            element<String>("status")
            element("url", UriSerializer.descriptor)
            element<List<Activity>>("activities")
            element<WorkspaceReference>("workspace")
        }

        override fun serialize(encoder: Encoder, value: Project) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeSerializableElement(descriptor, 0, DateSerializer, value.deliveryTime)
            compositeOutput.encodeStringElement(descriptor, 1, value.description)
            compositeOutput.encodeStringElement(descriptor, 2, value.projectName)
            compositeOutput.encodeStringElement(descriptor, 3, value.status)
            compositeOutput.encodeSerializableElement(descriptor, 4, UriSerializer, value.url)
            compositeOutput.encodeSerializableElement(
                descriptor,
                5,
                ListSerializer(Activity.serializer()),
                value.activities
            )
            compositeOutput.encodeSerializableElement(
                descriptor,
                6,
                WorkspaceReference.serializer(),
                value.workspace
            )
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Project {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var deliveryTime: Date? = null
            var description: String? = null
            var projectName: String? = null
            var status: String? = null
            var url: Uri? = null
            var activities: List<Activity>? = null
            var workspace: WorkspaceReference? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> deliveryTime = dec.decodeSerializableElement(descriptor, 0, DateSerializer)
                    1 -> description = dec.decodeStringElement(descriptor, 1)
                    2 -> projectName = dec.decodeStringElement(descriptor, 2)
                    3 -> status = dec.decodeStringElement(descriptor, 3)
                    4 -> url = dec.decodeSerializableElement(descriptor, 4, UriSerializer)
                    5 -> activities = dec.decodeSerializableElement(
                        descriptor, 5, ListSerializer(Activity.serializer())
                    )
                    6 -> workspace = dec.decodeSerializableElement(
                            descriptor, 6, WorkspaceReference.serializer()
                    )
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return Project(
                deliveryTime ?: throw MissingFieldException("deliveryTime"),
                description ?: throw MissingFieldException("description"),
                projectName ?: throw MissingFieldException("name"),
                status ?: throw MissingFieldException("status"),
                url ?: throw MissingFieldException("url"),
                activities ?: throw MissingFieldException("activities"),
                workspace ?: throw MissingFieldException("workspace")
            )
        }
    }
}
