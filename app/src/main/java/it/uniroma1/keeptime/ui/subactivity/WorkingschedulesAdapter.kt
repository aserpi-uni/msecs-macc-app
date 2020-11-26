package it.uniroma1.keeptime.ui.subactivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.Workingschedule
import java.text.SimpleDateFormat
import java.util.*

// TODO: use Material's one-line list when it will become public
class WorkingschedulesAdapter(workingschedules_: List<Workingschedule>, private val onClick: (Workingschedule) -> Any) :
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
            .inflate(R.layout.workingschedule_list_item, parent, false) as ConstraintLayout

        // set the view's size, margins, padding and layout parameters
        return WorkingscheduleViewHolder(
            constraintView
        )
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: WorkingscheduleViewHolder, position: Int) {
        val workingschedule = workingschedules[position]

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        (holder.constraintLayout.getViewById(R.id.workingschedulesListItemDate) as MaterialTextView).text = sdf.format(workingschedule.date)
        (holder.constraintLayout.getViewById(R.id.workingschedulesListItemHours) as MaterialTextView).text = workingschedule.hours.toString()

        holder.constraintLayout.setOnClickListener {
            onClick(workingschedule)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = workingschedules.size

    fun replace(subactivities_: List<Workingschedule>) {
        workingschedules.clear()
        workingschedules.addAll(subactivities_)
        notifyDataSetChanged()
    }
}
