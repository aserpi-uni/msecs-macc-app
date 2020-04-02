package it.uniroma1.keeptime.data.model

import android.net.Uri
import kotlinx.serialization.Serializable

import it.uniroma1.keeptime.data.UriSerializer


/**
 * Base class for clients. It contains only the client's url and its name.
 */
@Serializable
open class ClientReference(val name: String, @Serializable(with = UriSerializer::class) val url: Uri)
