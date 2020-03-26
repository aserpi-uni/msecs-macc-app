package it.uniroma1.keeptime.ui

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorText")
fun TextInputLayout.bindErrorMessage(errorMessage: Int?) {
    this.error = if(errorMessage == null) null else this.context.getString(errorMessage)
}


@BindingAdapter("visible")
fun View.visibleOrGone(visible: Boolean) {
    this.visibility = if(visible) View.VISIBLE else View.GONE
}
