package it.uniroma1.keeptime.ui.workers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.WorkerReference

// TODO: use Material's one-line list when it will become public
class WorkersAdapter(workers_: List<WorkerReference>) :
    RecyclerView.Adapter<WorkersAdapter.WorkerViewHolder>() {

    private val workers = workers_.toMutableList()

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class WorkerViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder {
        // create a new view
        val constraintView = LayoutInflater.from(parent.context)
            .inflate(R.layout.worker_list_item, parent, false) as ConstraintLayout

        // set the view's size, margins, padding and layout parameters
        return WorkerViewHolder(constraintView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        val worker = workers[position]
        (holder.constraintLayout.getViewById(R.id.workersListItemText) as MaterialTextView).text = worker.email
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = workers.size

    fun replace(workers_: List<WorkerReference>) {
        workers.clear()
        workers.addAll(workers_)
    }
}
