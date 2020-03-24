package it.uniroma1.keeptime.data.model

import android.net.Uri
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.Serializable

/**
 * Base class for workspaces. It contains only the workspace's url and its name.
 */
@Serializable
open class WorkspaceReference(var name: String, @Serializable(with = UriSerializer::class) val url: Uri)
