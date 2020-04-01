package it.uniroma1.keeptime.ui

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.busy(busy: Boolean) {
    this.isRefreshing = busy
}

@BindingAdapter("errorText")
fun TextInputLayout.bindErrorMessage(errorMessage: Int?) {
    this.error = if(errorMessage == null) null else this.context.getString(errorMessage)
}

@BindingAdapter("focused")
fun View.focused(focused: Boolean) {
    if(focused)
        this.requestFocus()
    else
        this.clearFocus()
}

@BindingAdapter("visible")
fun View.visibleOrGone(visible: Boolean) {
    this.visibility = if(visible) View.VISIBLE else View.GONE
}
