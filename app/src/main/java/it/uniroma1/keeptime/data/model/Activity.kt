package it.uniroma1.keeptime.data.model

import android.net.Uri
import it.uniroma1.keeptime.data.DateSerializer
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.*
import java.util.*


/**
 * Class for activities.
 */
@Serializable
open class Activity(
    deliveryTime: Date,
    description: String,
    status: String,
    url: Uri,
    val project: ProjectReference,
    val subactivities: List<SubactivityReference>
) : ActivityReference(deliveryTime, description, status, url) {

    @Serializer(forClass = Activity::class)
    companion object : INetwork<Activity> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Activity") {
            element("deliveryTime", DateSerializer.descriptor)
            element<String>("description")
            element<String>("status")
            element("url", UriSerializer.descriptor)
            element<ProjectReference>("project")
            element<List<SubactivityReference>>("subactivities")
        }

        override fun serialize(encoder: Encoder, value: Activity) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeSerializableElement(descriptor, 0, DateSerializer, value.deliveryTime)
            compositeOutput.encodeStringElement(descriptor, 1, value.description)
            compositeOutput.encodeStringElement(descriptor, 2, value.status)
            compositeOutput.encodeSerializableElement(descriptor, 3, UriSerializer, value.url)
            compositeOutput.encodeSerializableElement(
                descriptor,
                4,
                ProjectReference.serializer(),
                value.project
            )
            compositeOutput.encodeSerializableElement(
                descriptor,
                5,
                ListSerializer(SubactivityReference.serializer()),
                value.subactivities
            )
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Activity {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var deliveryTime: Date? = null
            var description: String? = null
            var status: String? = null
            var url: Uri? = null
            var project: ProjectReference? = null
            var subactivities: List<SubactivityReference>? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> deliveryTime = dec.decodeSerializableElement(descriptor, 0, DateSerializer)
                    1 -> description = dec.decodeStringElement(descriptor, 1)
                    2 -> status = dec.decodeStringElement(descriptor, 2)
                    3 -> url = dec.decodeSerializableElement(descriptor, 3, UriSerializer)
                    4 -> project = dec.decodeSerializableElement(
                            descriptor, 4, ProjectReference.serializer()
                    )
                    5 -> subactivities = dec.decodeSerializableElement(
                        descriptor,
                        5,
                        ListSerializer(SubactivityReference.serializer())
                    )
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return Activity(
                deliveryTime ?: throw MissingFieldException("deliveryTime"),
                description ?: throw MissingFieldException("description"),
                status ?: throw MissingFieldException("status"),
                url ?: throw MissingFieldException("url"),
                project ?: throw MissingFieldException("project"),
                subactivities ?: throw MissingFieldException("subactivities")
            )
        }
    }
}
