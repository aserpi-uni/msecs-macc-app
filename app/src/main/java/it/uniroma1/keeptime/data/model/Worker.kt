package it.uniroma1.keeptime.data.model

import android.icu.util.Currency
import android.net.Uri
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import org.json.JSONArray
import org.json.JSONObject

import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest


open class Worker(
    var bill_rate_cents: Int,
    currency_: String,
    email: String,
    url_: String,
    workspaces_: JSONArray
) :
    WorkerReference(email, url_) {

    companion object {
        fun getFromServer(url: String, successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
            val loginRequest = AuthenticatedJsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response -> returnWorker(response, successCallback) },
                Response.ErrorListener { error -> failCallback(error) })

            KeepTime.instance!!.requestQueue.add(loginRequest)
        }

        fun getFromServer(url: Uri, successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
            getFromServer(url.toString(), successCallback, failCallback)
        }

        private fun returnWorker(response: JSONObject, callback: (Worker) -> Any) {
            callback(
                Worker(
                    response.getInt("bill_rate_cents"),
                    response.getString("currency"),
                    response.getString("email"),
                    response.getString("url"),
                    response.getJSONArray("workspaces")
                )
            )
        }
    }

    val bill_rate: Number
        get() = bill_rate_cents / 100

    var currency: Currency = Currency.getInstance(currency_)

    val workspaces: List<WorkspaceReference> = WorkspaceReference.fromJsonArray(workspaces_)
}
