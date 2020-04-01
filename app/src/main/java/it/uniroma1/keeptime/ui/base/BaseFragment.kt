package it.uniroma1.keeptime.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import it.uniroma1.keeptime.LoginActivity
import it.uniroma1.keeptime.data.LoginRepository

open class BaseFragment : Fragment() {

    protected open lateinit var viewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.logoutMessage.observe(viewLifecycleOwner, Observer {
            val logoutMessage = it ?: return@Observer

            LoginRepository.removeCredentials()
            val loginIntent = Intent(context, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            loginIntent.putExtra("message", logoutMessage)
            startActivity(loginIntent)

            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        })

        viewModel.message.observe(viewLifecycleOwner, Observer {
            val message = it ?: return@Observer
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        })
    }
}
