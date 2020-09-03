package it.uniroma1.keeptime.ui.modals

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView
import java.util.*

import it.uniroma1.keeptime.R


class ModalBottomSheet(private val description: String, private val status: String?, private val deliveryDate: Date?) : BottomSheetDialogFragment() {
    constructor(description: String) : this(description, null, null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.modal_bottom_sheet, container, false)
        view.findViewById<MaterialTextView>(R.id.modal_bottom_sheet_text).text = description

        val statusColor: Int = when {
            status == "finished" -> Color.GREEN
            status == "undefined" -> Color.GRAY
            deliveryDate?.after(Date()) != null -> Color.RED
            else -> Color.GREEN
        }
        view.findViewById<ImageView>(R.id.modal_bottom_sheet_status).setColorFilter(statusColor)

        return view
    }
}
