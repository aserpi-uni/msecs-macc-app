package it.uniroma1.keeptime.ui.modals

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView
import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.ui.statusColor
import java.util.*


class ModalBottomSheet(
    private val clientColor: Int?,
    private val clientName: String?,
    private val description: String,
    private val deliveryDate: Date?,
    private val status: String?,
    private val workspaceName: String?
) : BottomSheetDialogFragment() {
    constructor(description: String) : this(null, null, description, null, null, null)
    constructor(description: String, status: String, deliveryDate: Date) : this(null, null, description, deliveryDate, status, null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.modal_bottom_sheet, container, false)
        view.findViewById<MaterialTextView>(R.id.modal_bottom_sheet_text).text = description

        val statusImage = view.findViewById<ImageView>(R.id.modal_bottom_sheet_status)
        if (status != null && deliveryDate != null) {
            statusImage.setColorFilter(statusColor(status, deliveryDate))
        } else {
            statusImage.visibility = View.GONE
        }

        val commissionText = view.findViewById<MaterialTextView>(R.id.modal_bottom_sheet_commission)
        if (clientColor != null && clientName != null && workspaceName != null) {
            val text = String.format("This project is followed by %s for <font color=\"%d\">%s</font>.", workspaceName, clientColor, clientName)
            commissionText.setText(Html.fromHtml(text, 0), TextView.BufferType.SPANNABLE)
            commissionText.visibility = View.VISIBLE
        }

        return view
    }
}
