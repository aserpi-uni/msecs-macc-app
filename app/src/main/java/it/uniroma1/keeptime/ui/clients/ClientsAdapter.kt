package it.uniroma1.keeptime.ui.clients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.model.ClientReference

// TODO: use Material's one-line list when it will become public
class ClientsAdapter(clients_: List<ClientReference>, private val onClick: (ClientReference) -> Any) :
    RecyclerView.Adapter<ClientsAdapter.ClientViewHolder>() {

    private val clients = clients_.toMutableList()

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ClientViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        // create a new view
        val constraintView = LayoutInflater.from(parent.context)
            .inflate(R.layout.client_list_item, parent, false) as ConstraintLayout

        // set the view's size, margins, padding and layout parameters
        return ClientViewHolder(constraintView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        val textView = holder.constraintLayout.getViewById(R.id.clientListItemText) as MaterialTextView

        textView.text = client.name
        if(client.color != null) textView.setTextColor(client.color)

        holder.constraintLayout.setOnClickListener {
            onClick(client)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = clients.size

    fun replace(clients_: List<ClientReference>) {
        clients.clear()
        clients.addAll(clients_)
        notifyDataSetChanged()
    }
}
