package it.uniroma1.keeptime.data.model

import android.net.Uri
import it.uniroma1.keeptime.data.DateSerializer
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.*
import java.util.*


/**
 * Class for activities.
 */
@Serializable
open class Activity(
    val deliveryTime: Date,
    val description: String,
    val status: String,
    val url: Uri,
    val project: ProjectReference) {

    @Serializer(forClass = Activity::class)
    companion object : INetwork<Activity> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Activity") {
            element("deliveryTime", DateSerializer.descriptor)
            element<String>("description")
            element<String>("status")
            element("url", UriSerializer.descriptor)
            element<ProjectReference>("project")
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
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Activity {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var deliveryTime: Date? = null
            var description: String? = null
            var status: String? = null
            var url: Uri? = null
            var project: ProjectReference? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> deliveryTime = dec.decodeSerializableElement(descriptor, 0, DateSerializer)
                    1 -> description = dec.decodeStringElement(descriptor, 1)
                    2 -> status = dec.decodeStringElement(descriptor, 3)
                    3 -> url = dec.decodeSerializableElement(descriptor, 4, UriSerializer)
                    4 -> project = dec.decodeSerializableElement(
                            descriptor, 4, ProjectReference.serializer()
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
                project ?: throw MissingFieldException("project")
            )
        }
    }
}
