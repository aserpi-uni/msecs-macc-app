package it.uniroma1.keeptime.data.model

import android.net.Uri
import org.json.JSONArray

open class WorkspaceReference(var name: String, url_: String) {

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

    val url = Uri.parse(url_)
}
