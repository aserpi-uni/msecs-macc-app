package it.uniroma1.keeptime.data.model

import android.icu.util.Currency
import android.net.Uri
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.data.AuthenticatedJsonObjectRequest
import org.json.JSONObject

open class Worker(var bill_rate_cents: Int, currency_: String, var email: String, url_: String) {
    companion object {
        fun getFromServer(url: String, successCallback: (Worker) -> Any, failCallback: (VolleyError) -> Any) {
            val loginRequest = AuthenticatedJsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response -> returnWorker(response, successCallback) },
                Response.ErrorListener { error ->  failCallback(error) })

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
                    response.getString("url")
                )
            )
        }
    }

    var currency: Currency = Currency.getInstance(currency_)
    val url: Uri = Uri.parse(url_)

    val bill_rate: Number
        get() = bill_rate_cents / 100
}
