package it.uniroma1.keeptime.ui.modals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView

import it.uniroma1.keeptime.R


class ModalBottomSheet(private val description: String) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.modal_bottom_sheet, container, false)
        view.findViewById<MaterialTextView>(R.id.modal_bottom_sheet_text).text = description

        return view
    }
}
