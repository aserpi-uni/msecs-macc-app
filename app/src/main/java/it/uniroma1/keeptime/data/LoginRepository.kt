package it.uniroma1.keeptime.data

import android.net.Uri
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.data.model.LoggedInUser
import org.json.JSONObject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository {

    companion object {
        val isLoggedIn: Boolean
            get() = user != null

        var user: LoggedInUser? = null
            private set

        private var server: String? = null
    }

    fun login(
        username: String, password: String, server_: String,
        successCallback: (LoggedInUser) -> Unit, failCallback: (VolleyError) -> Unit
    ) {
        val serverBuilder = Uri.parse(server_).buildUpon()
        serverBuilder.scheme("https")
        server = serverBuilder.build().toString()
        serverBuilder.appendPath("workers").appendPath("sign_in.json")

        val requestParameters = JSONObject("{\"worker\":{\"email\":\"$username\",\"password\":\"$password\"}}")
        val loginRequest = JsonObjectRequest(
            Request.Method.POST, serverBuilder.build().toString(), requestParameters,
            Response.Listener { response -> onLoginSuccess(response, successCallback) },
            Response.ErrorListener { error -> onLoginFailure(error, failCallback) })

        NetworkRequestSingleton.getInstance(KeepTime.context).addToRequestQueue(loginRequest, false)
    }

    fun logout(successCallback: () -> Unit, failCallback: (VolleyError) -> Unit) {
        val serverBuilder = Uri.parse(server).buildUpon()
        serverBuilder.appendPath("workers").appendPath("sign_out.json")

        val logoutRequest = JsonObjectRequest(
            Request.Method.DELETE, serverBuilder.build().toString(), null,
            Response.Listener { successCallback() }, Response.ErrorListener { error -> failCallback(error) }
        )
        NetworkRequestSingleton.getInstance(KeepTime.context).addToRequestQueue(logoutRequest)
    }

    fun onLoginFailure(error: VolleyError, callback: (VolleyError) -> Unit) {
        user = null
        callback(error)
    }

    fun onLoginSuccess(response: JSONObject, callback: (LoggedInUser) -> Unit) {
        user = LoggedInUser(
            response.getString("authentication_token"),
            response.getString("email"),
            response.getString("url")
        )
        callback(user!!)
    }
}
