package it.uniroma1.keeptime.data

import android.icu.util.Currency
import android.net.Uri
import kotlinx.serialization.*

@Serializer(forClass = Uri::class)
object UriSerializer: KSerializer<Uri> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Uri", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }
}

@Serializer(forClass = Currency::class)
object CurrencySerializer: KSerializer<Currency> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Currency", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Currency) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Currency {
        return Currency.getInstance(decoder.decodeString())
    }
}
