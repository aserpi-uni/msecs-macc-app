package it.uniroma1.keeptime.data

import android.icu.util.Currency
import android.net.Uri
import kotlinx.serialization.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Serializer for colors.
 */
@Serializer(forClass = Int::class)
object ColorSerializer: KSerializer<Int?> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Color", PrimitiveKind.INT)

    // -16777216 (0xFF000000) to add opacity

    override fun serialize(encoder: Encoder, value: Int?) {
        if(value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeInt(value + 16777216)
        }
    }

    override fun deserialize(decoder: Decoder): Int? {
        return try {
            decoder.decodeInt() - 16777216
        } catch (e: Exception) {
            decoder.decodeNull()
        }
    }
}

/**
 * Serializer for dates.
 */
@Serializer(forClass = Date::class)
object DateSerializer: KSerializer<Date> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        encoder.encodeString(sdf.format(value))
    }

    override fun deserialize(decoder: Decoder): Date {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.parse(decoder.decodeString())!!
    }
}

/**
 * Serializer for [Uri].
 */
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

/**
 * Serializer for [Currency].
 */
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
