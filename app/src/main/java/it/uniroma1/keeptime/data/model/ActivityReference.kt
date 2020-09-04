package it.uniroma1.keeptime.data.model

import android.net.Uri
import it.uniroma1.keeptime.data.DateSerializer
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.*
import java.util.*


/**
 * Base class for projects. It contains only the project's properties (no associations).
 */
@Serializable
open class ActivityReference(
    val deliveryTime: Date,
    val description: String,
    val status: String,
    val url: Uri) {

    @Serializer(forClass = ActivityReference::class)
    companion object : INetwork<ActivityReference> {
        override val descriptor: SerialDescriptor = SerialDescriptor("ActivityReference") {
            element("deliveryTime", DateSerializer.descriptor)
            element<String>("description")
            element<String>("status")
            element("url", UriSerializer.descriptor)
        }

        override fun serialize(encoder: Encoder, value: ActivityReference) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeSerializableElement(descriptor, 0, DateSerializer, value.deliveryTime)
            compositeOutput.encodeStringElement(descriptor, 1, value.description)
            compositeOutput.encodeStringElement(descriptor, 2, value.status)
            compositeOutput.encodeSerializableElement(descriptor, 3, UriSerializer, value.url)
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): ActivityReference {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var deliveryTime: Date? = null
            var description: String? = null
            var status: String? = null
            var url: Uri? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> deliveryTime = dec.decodeSerializableElement(descriptor, 0, DateSerializer)
                    1 -> description = dec.decodeStringElement(descriptor, 1)
                    2 -> status = dec.decodeStringElement(descriptor, 3)
                    3 -> url = dec.decodeSerializableElement(descriptor, 4, UriSerializer)
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return ActivityReference(
                deliveryTime ?: throw MissingFieldException("deliveryTime"),
                description ?: throw MissingFieldException("description"),
                status ?: throw MissingFieldException("status"),
                url ?: throw MissingFieldException("url")
            )
        }
    }
    suspend fun fromServer(): Activity = Activity.fromServer(url)
}
