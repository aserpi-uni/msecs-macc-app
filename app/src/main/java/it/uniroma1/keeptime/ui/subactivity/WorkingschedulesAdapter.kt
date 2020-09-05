package it.uniroma1.keeptime.ui.subactivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.WorkingscheduleReference

// TODO: use Material's one-line list when it will become public
class WorkingschedulesAdapter(workingschedules_: List<WorkingscheduleReference>, private val onClick: (WorkingscheduleReference) -> Any) :
    RecyclerView.Adapter<WorkingschedulesAdapter.WorkingscheduleViewHolder>() {

    private val workingschedules = workingschedules_.toMutableList()

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class WorkingscheduleViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkingscheduleViewHolder {
        // create a new view
        val constraintView = LayoutInflater.from(parent.context)
            .inflate(R.layout.subactivity_list_item, parent, false) as ConstraintLayout

        // set the view's size, margins, padding and layout parameters
        return WorkingscheduleViewHolder(
            constraintView
        )
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: WorkingscheduleViewHolder, position: Int) {
        val workingschedule = workingschedules[position]
        (holder.constraintLayout.getViewById(R.id.activitiesListItemText) as MaterialTextView).text = workingschedule.toString()

        holder.constraintLayout.setOnClickListener {
            onClick(workingschedule)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = workingschedules.size

    fun replace(subactivities_: List<WorkingscheduleReference>) {
        workingschedules.clear()
        workingschedules.addAll(subactivities_)
        notifyDataSetChanged()
    }
}
