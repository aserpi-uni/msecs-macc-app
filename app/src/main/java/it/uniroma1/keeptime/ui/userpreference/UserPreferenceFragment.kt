package it.uniroma1.keeptime.ui.userpreference

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import it.uniroma1.keeptime.LoginActivity

import it.uniroma1.keeptime.R
import it.uniroma1.keeptime.data.LoginRepository

class UserPreferenceFragment : Fragment() {

    companion object {
        fun newInstance() = UserPreferenceFragment()
    }

    private lateinit var viewModel: UserPreferenceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_preference_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserPreferenceViewModel::class.java)

        viewModel.logoutResult.observe(viewLifecycleOwner, Observer {
            val logoutResult = it ?: return@Observer
            val loading = activity?.findViewById<ProgressBar>(R.id.loading)

            if(logoutResult.first) {
                LoginRepository().removeCredentials()
                val loginIntent = Intent(context, LoginActivity::class.java)
                loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(loginIntent)

                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            } else view?.let {
                loading?.visibility = View.GONE
                Snackbar.make(view!!, logoutResult.second!!, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menu.findItem(R.id.action_settings).isVisible = false

        val logoutItem = menu.findItem(R.id.action_logout)
        logoutItem.isVisible = true
        logoutItem.setOnMenuItemClickListener {
            activity?.findViewById<ProgressBar>(R.id.loading)?.visibility = View.VISIBLE
            viewModel.logout()
            true
        }
    }
}
