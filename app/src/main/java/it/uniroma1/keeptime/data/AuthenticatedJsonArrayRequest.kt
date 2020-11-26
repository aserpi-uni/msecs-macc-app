package it.uniroma1.keeptime.data

import android.net.Uri
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonRequest
import org.json.JSONArray


/**
 * Creates a new authenticated request containing JSON payload.
 *
 * @param method the HTTP method to use
 * @param url URL to fetch the JSON from
 * @param jsonRequest A JSONObject to post with the request.
 * Null indicates no parameters will be posted along with request.
 * @param listener Listener to receive the JSON response
 * @param errorListener Error listener, or null to ignore errors
 */
class AuthenticatedJsonArrayRequest(
    method: Int,
    url: String,
    jsonRequest: JSONArray?,
    listener: Response.Listener<JSONArray>,
    errorListener: Response.ErrorListener
) : JsonArrayRequest(method, url, jsonRequest, listener, errorListener) {

    /**
     * @see AuthenticatedJsonObjectRequest
     */
    constructor(
        method: Int,
        url: Uri,
        jsonRequest: JSONArray?,
        listener: Response.Listener<JSONArray>,
        errorListener: Response.ErrorListener
    ) : this(method, url.toString(), jsonRequest, listener, errorListener)

    @Throws(AuthFailureError::class)
    override fun getHeaders(): MutableMap<String, String>? {
        val headers = HashMap<String, String>()
        headers["X-WORKER-EMAIL"] = LoginRepository.user.value!!.email
        headers["X-WORKER-TOKEN"] = LoginRepository.authenticationToken!!
        return headers
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONArray?> {
        val jsonString = String(
            response.data,
            charset(HttpHeaderParser.parseCharset(response.headers, JsonRequest.PROTOCOL_CHARSET))
        )

        if(jsonString.isEmpty()) return Response.success(null, HttpHeaderParser.parseCacheHeaders(response))
        return super.parseNetworkResponse(response)
    }
}
