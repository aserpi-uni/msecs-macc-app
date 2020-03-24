package it.uniroma1.keeptime.data.model

import android.net.Uri
import it.uniroma1.keeptime.data.UriSerializer
import kotlinx.serialization.Serializable
import org.json.JSONArray

@Serializable
open class WorkspaceReference(var name: String, @Serializable(with = UriSerializer::class) val url: Uri) {

    companion object {
        fun fromJsonArray(workspacesJson: JSONArray): List<WorkspaceReference> {
            val workspaces = ArrayList<WorkspaceReference>(workspacesJson.length())
            for(i in 0 until workspacesJson.length()) {
                val workspaceJson = workspacesJson.getJSONObject(i)
                workspaces.add(
                    WorkspaceReference(workspaceJson.getString("name"), workspaceJson.getString("url"))
                )
            }

            return workspaces
        }
    }

    constructor(name: String, url: String) : this(name, Uri.parse(url))
}
