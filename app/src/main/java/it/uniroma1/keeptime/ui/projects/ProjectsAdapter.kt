package it.uniroma1.keeptime.ui.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.ProjectReference

// TODO: use Material's one-line list when it will become public
class ProjectsAdapter(projects_: List<ProjectReference>, private val onClick: (ProjectReference) -> Any) :
    RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {

    private val projects = projects_.toMutableList()

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ProjectViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        // create a new view
        val constraintView = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_list_item, parent, false) as ConstraintLayout

        // set the view's size, margins, padding and layout parameters
        return ProjectViewHolder(constraintView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        (holder.constraintLayout.getViewById(R.id.projectsListItemText) as MaterialTextView).text = project.projectName

        holder.constraintLayout.setOnClickListener {
            onClick(project)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = projects.size

    fun replace(projects_: List<ProjectReference>) {
        projects.clear()
        projects.addAll(projects_)
        notifyDataSetChanged()
    }
}
