package it.uniroma1.keeptime.ui.modals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.ui.statusColor
import java.util.*


class ModalBottomSheet(private val description: String, private val status: String?, private val deliveryDate: Date?) : BottomSheetDialogFragment() {
    constructor(description: String) : this(description, null, null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.modal_bottom_sheet, container, false)
        view.findViewById<MaterialTextView>(R.id.modal_bottom_sheet_text).text = description

        val statusImage = view.findViewById<ImageView>(R.id.modal_bottom_sheet_status)
        if (status != null && deliveryDate != null) {
            statusImage.setColorFilter(statusColor(status, deliveryDate))
        } else {
            statusImage.visibility = View.GONE
        }

        return view
    }
}
