package it.uniroma1.keeptime.data.model

import android.icu.util.Currency
import android.net.Uri
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer

import it.uniroma1.keeptime.data.CurrencySerializer
import it.uniroma1.keeptime.data.UriSerializer

/**
 * Class for workers.
 */
@Serializable
open class Worker(
    var billRate: Double,
    var currency: Currency,
    email: String,
    url: Uri,
    val clients: List<ClientReference>,
    val workspaces: List<WorkspaceReference>
) :
    WorkerReference(email, url) {

    @Serializer(forClass = Worker::class)
    companion object : INetwork<Worker> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Worker") {
            element<Int>("bill_rate_cents")
            element("currency", CurrencySerializer.descriptor)
            element<String>("email")
            element("url", UriSerializer.descriptor)
            element<List<ClientReference>>("clients")
            element<List<WorkspaceReference>>("workspaces")
        }

        override fun serialize(encoder: Encoder, value: Worker) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeIntElement(descriptor, 0, (value.billRate * 100).toInt())
            compositeOutput.encodeSerializableElement(descriptor, 1, CurrencySerializer, value.currency)
            compositeOutput.encodeStringElement(descriptor, 2, value.email)
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
                ListSerializer(WorkspaceReference.serializer()),
                value.workspaces
            )
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): Worker {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var billRate: Double? = null
            var currency: Currency? = null
            var email: String? = null
            var url: Uri? = null
            var clients: List<ClientReference>? = null
            var workspaces: List<WorkspaceReference>? = null
            loop@ while (true) {
                when (val i = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> billRate = dec.decodeIntElement(descriptor, 0) / 100.0
                    1 -> currency = dec.decodeSerializableElement(descriptor, 1, CurrencySerializer)
                    2 -> email = dec.decodeStringElement(descriptor, 2)
                    3 -> url = dec.decodeSerializableElement(descriptor, 3, UriSerializer)
                    4 -> clients =
                        dec.decodeSerializableElement(descriptor, 4, ListSerializer(ClientReference.serializer()))
                    5 -> workspaces =
                        dec.decodeSerializableElement(
                            descriptor, 5, ListSerializer(WorkspaceReference.serializer())
                        )
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            dec.endStructure(descriptor)
            return Worker(
                billRate ?: throw MissingFieldException("bill_rate_cents"),
                currency ?: throw MissingFieldException("currency"),
                email ?: throw MissingFieldException("email"),
                url ?: throw MissingFieldException("url"),
                clients ?: throw MissingFieldException("clients"),
                workspaces ?: throw MissingFieldException("workspaces")
            )
        }
    }
}
