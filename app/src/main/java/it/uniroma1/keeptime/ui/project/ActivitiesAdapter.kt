package it.uniroma1.keeptime.ui.project

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.ActivityReference
import it.uniroma1.keeptime.ui.statusColor

// TODO: use Material's one-line list when it will become public
class ActivitiesAdapter(activities_: List<ActivityReference>, private val onClick: (ActivityReference) -> Any) :
    RecyclerView.Adapter<ActivitiesAdapter.ActivityViewHolder>() {

    private val activities = activities_.toMutableList()

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ActivityViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        // create a new view
        val constraintView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_list_item, parent, false) as ConstraintLayout

        // set the view's size, margins, padding and layout parameters
        return ActivityViewHolder(constraintView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]

        (holder.constraintLayout.getViewById(R.id.activitiesListItemText) as MaterialTextView).text = activity.description
        holder.constraintLayout.setOnClickListener { onClick(activity) }

        (holder.constraintLayout.getViewById(R.id.activitiesStatus) as ImageView).setColorFilter(statusColor(activity.status, activity.deliveryTime))
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = activities.size

    fun replace(activities_: List<ActivityReference>) {
        activities.clear()
        activities.addAll(activities_)
        notifyDataSetChanged()
    }
}
