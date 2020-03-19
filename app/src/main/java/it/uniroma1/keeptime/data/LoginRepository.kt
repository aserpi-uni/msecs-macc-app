package it.uniroma1.keeptime.data

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
        var user: LoggedInUser? = null
            private set

        val isLoggedIn: Boolean
            get() = user != null
    }

    fun login(username: String, password: String, server: String,
              successCallback: (LoggedInUser) -> Unit, failCallback : (VolleyError) -> Unit) {
        //TODO: server formatting
        val requestParameters = JSONObject("{\"worker\":{\"email\":\"$username\",\"password\":\"$password\"}}")
        val loginRequest = JsonObjectRequest(
            Request.Method.POST, "$server/workers/sign_in.json", requestParameters,
            Response.Listener { response -> onLoginSuccess(response, successCallback) },
            Response.ErrorListener { error -> onLoginFailure(error, failCallback) })

        NetworkRequestSingleton.getInstance(KeepTime.context).addToRequestQueue(loginRequest, false)
    }

    fun logout() {
        user = null
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
