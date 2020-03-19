package it.uniroma1.keeptime.data.model

import android.net.Uri
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest
import org.json.JSONObject

data class Worker(val bill_rate_cents: Int, val currency: String,  val email: String, val url: Uri) {
    companion object {
        fun getFromServer(url: String, successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
            val loginRequest = AuthenticatedJsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response -> returnWorker(response, successCallback) },
                Response.ErrorListener { error ->  failCallback(error) })

            KeepTime.instance!!.requestQueue.add(loginRequest)
        }

        private fun returnWorker(response: JSONObject, callback: (Worker) -> Any) {
            callback(
                Worker(
                    response.getInt("bill_rate_cents"),
                    response.getString("currency"),
                    response.getString("email"),
                    Uri.parse(response.getString("url"))
                )
            )
        }
    }
}
