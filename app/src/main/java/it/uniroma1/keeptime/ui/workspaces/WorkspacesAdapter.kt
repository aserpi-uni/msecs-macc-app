package it.uniroma1.keeptime.ui.workspaces

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.WorkspaceReference

// TODO: use Material's one-line list when it will become public
class WorkspacesAdapter(private val workspaces: List<WorkspaceReference>) :
    RecyclerView.Adapter<WorkspacesAdapter.WorkspaceViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class WorkspaceViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceViewHolder {
        // create a new view
        val constraintView = LayoutInflater.from(parent.context)
            .inflate(R.layout.workspace_list_item, parent, false) as ConstraintLayout

        // set the view's size, margins, padding and layout parameters
        return WorkspaceViewHolder(constraintView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: WorkspaceViewHolder, position: Int) {
        (holder.constraintLayout.getViewById(R.id.workspacesListItemText) as MaterialTextView).text =
            workspaces[position].name
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = workspaces.size
}
