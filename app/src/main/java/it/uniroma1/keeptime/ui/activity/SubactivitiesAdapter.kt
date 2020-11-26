package it.uniroma1.keeptime.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.SubactivityReference
import it.uniroma1.keeptime.ui.statusColor

// TODO: use Material's one-line list when it will become public
class SubactivitiesAdapter(subactivities_: List<SubactivityReference>, private val onClick: (SubactivityReference) -> Any) :
    RecyclerView.Adapter<SubactivitiesAdapter.SubactivityViewHolder>() {

    private val subactivities = subactivities_.toMutableList()

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class SubactivityViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubactivityViewHolder {
        // create a new view
        val constraintView = LayoutInflater.from(parent.context)
            .inflate(R.layout.subactivity_list_item, parent, false) as ConstraintLayout

        // set the view's size, margins, padding and layout parameters
        return SubactivityViewHolder(
            constraintView
        )
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: SubactivityViewHolder, position: Int) {
        val subactivity = subactivities[position]

        (holder.constraintLayout.getViewById(R.id.subactivitiesListItemText) as MaterialTextView).text = subactivity.description
        holder.constraintLayout.setOnClickListener { onClick(subactivity) }

        (holder.constraintLayout.getViewById(R.id.subactivitiesStatus) as ImageView).setColorFilter(statusColor(subactivity.status, subactivity.deliveryTime))
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = subactivities.size

    fun replace(subactivities_: List<SubactivityReference>) {
        subactivities.clear()
        subactivities.addAll(subactivities_)
        notifyDataSetChanged()
    }
}
