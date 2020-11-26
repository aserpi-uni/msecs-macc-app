package it.uniroma1.keeptime.data.model

import android.net.Uri
import it.uniroma1.keeptime.data.ColorSerializer
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.Serializable

/**
 * Base class for projects. It contains only the project's url and its name.
 */
@Serializable
open class ProjectReference(
    @Serializable(with = ColorSerializer::class) val color: Int?,
    var projectName: String,
    @Serializable(with = UriSerializer::class) val url: Uri) {

    /**
     * Retrieves the workspace's attributes from the server.
     */
    suspend fun fromServer(): Project = Project.fromServer(url)
}
