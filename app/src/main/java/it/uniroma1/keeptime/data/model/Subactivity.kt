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
open class Subactivity(
    deliveryTime: Date,
    description: String,
    val master: Boolean,
    status: String,
    val activity: ActivityReference,
    val worker_1: WorkerReference,
    val worker_2: WorkerReference,
    val worker_3: WorkerReference,
    val workingschedules: List<Workingschedule>,
    url: Uri
    ) : SubactivityReference(deliveryTime, description, status, url) {

    @Serializer(forClass = Subactivity::class)
    companion object : INetwork<Subactivity> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Subactivity") {
            element("deliveryTime", DateSerializer.descriptor)
            element<String>("description")
            element<Boolean>("master")
            element<String>("status")
            element<ActivityReference>("activity")
            element<WorkerReference>("worker_1")
            element<WorkerReference>("worker_2")
            element<WorkerReference>("worker_3")
            element<List<Workingschedule>>("workingschedules")
            element("url", UriSerializer.descriptor)
        }

        override fun serialize(encoder: Encoder, value: Subactivity) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeSerializableElement(descriptor, 0, DateSerializer, value.deliveryTime)
            compositeOutput.encodeStringElement(descriptor, 1, value.description)
            compositeOutput.encodeBooleanElement(descriptor, 2, value.master)
            compositeOutput.encodeStringElement(descriptor, 3, value.status)
            compositeOutput.encodeSerializableElement(
                descriptor,
                4,
                ActivityReference.serializer(),
                value.activity
            )
            compositeOutput.encodeSerializableElement(
                descriptor,
                5,
                WorkerReference.serializer(),
                value.worker_1
            )
            compositeOutput.encodeSerializableElement(
                descriptor,
                6,
                WorkerReference.serializer(),
                value.worker_2
            )
            compositeOutput.encodeSerializableElement(
                descriptor,
                7,
                WorkerReference.serializer(),
                value.worker_3
            )
            compositeOutput.encodeSerializableElement(
                descriptor,
                8,
                ListSerializer(Workingschedule.serializer()),
                value.workingschedules
            )
            compositeOutput.encodeSerializableElement(descriptor, 9, UriSerializer, value.url)
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Subactivity {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var deliveryTime: Date? = null
            var description: String? = null
            var master: Boolean? = null
            var status: String? = null
            var activity: ActivityReference? = null
            var worker_1: WorkerReference? = null
            var worker_2: WorkerReference? = null
            var worker_3: WorkerReference? = null
            var workingschedules: List<Workingschedule>? = null
            var url: Uri? = null
            loop@ while(true) {
                when(val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> deliveryTime = dec.decodeSerializableElement(descriptor, 0, DateSerializer)
                    1 -> description = dec.decodeStringElement(descriptor, 1)
                    2 -> master = dec.decodeBooleanElement(descriptor, 2)
                    3 -> status = dec.decodeStringElement(descriptor, 3)
                    4 -> activity = dec.decodeSerializableElement(descriptor, 4, ActivityReference.serializer())
                    5 -> worker_1 = dec.decodeSerializableElement(descriptor, 5, WorkerReference.serializer())
                    6 -> worker_2 = dec.decodeSerializableElement(descriptor, 6, WorkerReference.serializer())
                    7 -> worker_3 = dec.decodeSerializableElement(descriptor, 7, WorkerReference.serializer())
                    8 -> workingschedules = dec.decodeSerializableElement(descriptor, 8, ListSerializer(Workingschedule.serializer()))
                    9 -> url = dec.decodeSerializableElement(descriptor, 9, UriSerializer)
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return Subactivity(
                deliveryTime ?: throw MissingFieldException("deliveryTime"),
                description ?: throw MissingFieldException("description"),
                master ?: throw MissingFieldException("master"),
                status ?: throw MissingFieldException("status"),
                activity?: throw MissingFieldException("activity"),
                worker_1?: throw MissingFieldException("worker_1"),
                worker_2 ?: throw MissingFieldException("worker_2"),
                worker_3 ?: throw MissingFieldException("worker_3"),
                workingschedules ?: throw MissingFieldException("workingschedules"),
                url ?: throw MissingFieldException("url")
            )
        }
    }
}
