package it.uniroma1.keeptime.data

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import org.json.JSONObject
import java.nio.charset.Charset


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
        headers["X-WORKER-TOKEN"] = LoginRepository.authenticationToken!!
        return headers
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject?> {
        val jsonString = String(
            response.data,
            charset(HttpHeaderParser.parseCharset(response.headers, JsonRequest.PROTOCOL_CHARSET))
        )

        if(jsonString.isEmpty()) return Response.success(null, HttpHeaderParser.parseCacheHeaders(response))
        return super.parseNetworkResponse(response)
    }
}
