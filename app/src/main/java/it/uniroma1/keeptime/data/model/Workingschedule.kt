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
open class Workingschedule(
    val date: Date,
    val hours: Int,
    val subactivity: SubactivityReference,
    url: Uri
) : WorkingscheduleReference(url) {

    @Serializer(forClass = Workingschedule::class)
    companion object : INetwork<Workingschedule> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Workingschedule") {
            element("date", DateSerializer.descriptor)
            element<Int>("hours")
            element<SubactivityReference>("subactivity")
            element("url", UriSerializer.descriptor)
        }

        override fun serialize(encoder: Encoder, value: Workingschedule) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeSerializableElement(descriptor, 0, DateSerializer, value.date)
            compositeOutput.encodeIntElement(descriptor, 1, value.hours)
            compositeOutput.encodeSerializableElement(descriptor, 2, SubactivityReference.serializer(), value.subactivity)
            compositeOutput.encodeSerializableElement(descriptor, 3, UriSerializer, value.url)
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Workingschedule {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var date: Date? = null
            var hours: Int? = null
            var subactivity: SubactivityReference? = null
            var url: Uri? = null

            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> date = dec.decodeSerializableElement(descriptor, 0, DateSerializer)
                    1 -> hours = dec.decodeIntElement(descriptor, 1)
                    2 -> subactivity = dec.decodeSerializableElement(descriptor, 3, SubactivityReference.serializer())
                    3 -> url = dec.decodeSerializableElement(descriptor, 4, UriSerializer)
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return Workingschedule(
                date ?: throw MissingFieldException("date"),
                hours ?: throw MissingFieldException("hours"),
                subactivity ?: throw MissingFieldException("subactivity"),
                url ?: throw MissingFieldException("url")
            )
        }
    }
}
