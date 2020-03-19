package it.uniroma1.keeptime.data

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


class AuthenticatedJsonObjectRequest(
    method: Int,
    url: String,
    jsonRequest: JSONObject?,
    listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener
) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {

    @Throws(AuthFailureError::class)
    override fun getHeaders(): MutableMap<String, String>? {
        val headers = HashMap<String, String>()
        headers["X-WORKER-EMAIL"] = LoginRepository.user!!.email
        headers["X-WORKER-TOKEN"] = LoginRepository.user!!.authenticationToken
        return headers
    }
}
