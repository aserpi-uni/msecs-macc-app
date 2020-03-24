package it.uniroma1.keeptime.data

import android.net.Uri
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.data.model.WorkerReference
import org.json.JSONObject
import java.io.File
import java.io.ObjectOutputStream


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository {

    companion object {
        val isLoggedIn: Boolean
            get() = user != null

        // TODO: secure
        var authenticationToken: String? = null
            private set

        var user: WorkerReference? = null

        private var server: String? = null
    }

    /**
     * Checks stored credentials to see if they are still valid.
     */
    fun checkCredentials(url: String, email_: String, authenticationToken_: String,
                         successCallback: (Worker) -> Unit, failCallback: (VolleyError) -> Unit) {
        authenticationToken = authenticationToken_
        user = WorkerReference(email_, url)
        server = Regex("(https:\\/\\/[^\\/]*)\\S*").matchEntire(url)!!.groupValues[1]
        user!!.fromServer(onWorkerSuccess(successCallback), onWorkerFailure(failCallback))
    }

    /**
     * Asks the server for its Google OAuth
     * [client ID](https://developers.google.com/identity/sign-in/android/backend-auth#send-the-id-token-to-your-server).
     *
     * @see googleOauthSignIn
     */
    fun googleOauthId(server_: String, successCallback: (String) -> Unit, failCallback: (VolleyError) -> Unit) {
        val serverBuilder = Uri.parse(if(server_.startsWith("https")) server_ else "https://$server_").buildUpon()
        server = serverBuilder.build().toString()
        serverBuilder.appendPath("google_oauth").appendPath("id")

        val loginRequest = StringRequest(
            Request.Method.GET, serverBuilder.build().toString(),
            Response.Listener { response: String -> successCallback(response) },
            Response.ErrorListener { error -> failCallback(error) })

        KeepTime.instance!!.requestQueue.add(loginRequest)
    }

    /**
     * Sends the Google [idToken](https://developers.google.com/identity/sign-in/android/backend-auth)
     * retrieved by [LoginActivity][it.uniroma1.keeptime.LoginActivity] to the server
     * in order to get an authentication token.
     */
    fun googleOauthSignIn(
        idToken: String,
        successCallback: (Worker) -> Unit,
        failCallback: (VolleyError) -> Unit
    ) {
        val loginRequest = JsonObjectRequest(
            Request.Method.POST,
            Uri.parse(server).buildUpon()
                .appendPath("google_oauth")
                .appendPath("sign_in.json")
                .build().toString(),
            JSONObject("{\"id_token\":\"$idToken\"}"),
            Response.Listener { response -> onLoginSuccess(response, successCallback, failCallback) },
            Response.ErrorListener { error -> onLoginFailure(error, failCallback) })

        KeepTime.instance!!.requestQueue.add(loginRequest)
    }

    /**
     * Logs a user in using email address and password.
     */
    fun login(
        username: String, password: String, server_: String,
        successCallback: (Worker) -> Unit, failCallback: (VolleyError) -> Unit
    ) {
        val serverBuilder = Uri.parse(if(server_.startsWith("https")) server_ else "https://$server_").buildUpon()
        server = serverBuilder.build().toString()
        serverBuilder.appendPath("workers").appendPath("sign_in.json")

        val requestParameters = JSONObject("{\"worker\":{\"email\":\"$username\",\"password\":\"$password\"}}")
        val loginRequest = JsonObjectRequest(
            Request.Method.POST, serverBuilder.build().toString(), requestParameters,
            Response.Listener { response -> onLoginSuccess(response, successCallback, failCallback) },
            Response.ErrorListener { error -> onLoginFailure(error, failCallback) })

        KeepTime.instance!!.requestQueue.add(loginRequest)
    }

    /**
     * Logs a user out.
     */
    fun logout(successCallback: () -> Unit, failCallback: (VolleyError) -> Unit) {
        val serverBuilder = Uri.parse(server).buildUpon()
        serverBuilder.appendPath("workers").appendPath("sign_out.json")

        val logoutRequest = AuthenticatedJsonObjectRequest(
            Request.Method.DELETE, serverBuilder.build().toString(), null,
            Response.Listener { successCallback() }, Response.ErrorListener { error -> failCallback(error) }
        )

        KeepTime.instance!!.requestQueue.add(logoutRequest)
    }

    private fun onLoginFailure(error: VolleyError, callback: (VolleyError) -> Unit) {
        removeCredentials()
        callback(error)
    }

    private fun onLoginSuccess(response: JSONObject, successCallback: (Worker) -> Unit, failCallback: (VolleyError) -> Unit) {
        authenticationToken = response.getString("authentication_token")
        user = WorkerReference(response.getString("email"), response.getString("url"))

        // Remove old credentials from local storage
        try {
            File(KeepTime.context.filesDir, "CredentialsFile").delete()
        } catch (e: java.io.IOException) { }

        // Save credentials in encrypted local storage
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val file = File(KeepTime.context.filesDir, "CredentialsFile")
        val encryptedFile = EncryptedFile.Builder(
            file,
            KeepTime.context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        encryptedFile.openFileOutput().use { outStream ->
            ObjectOutputStream(outStream).use { outObjStream ->
                outObjStream.writeObject(user!!.url.toString())
                outObjStream.writeObject(user!!.email)
                outObjStream.writeObject(authenticationToken!!)
            }
        }

        user!!.fromServer(onWorkerSuccess(successCallback), onWorkerFailure(failCallback))
    }

    private fun onWorkerFailure(failCallback: (VolleyError) -> Unit): (VolleyError) -> Any {
        return { error: VolleyError -> removeCredentials(); failCallback(error) }
    }

    private fun onWorkerSuccess(successCallback: (Worker) -> Unit): (Worker) -> Any {
        return { worker: Worker -> user = worker; successCallback(worker) }
    }

    /**
     * Removes stored credentials.
     */
    fun removeCredentials() {
        authenticationToken = null
        user = null
        server = null

        // Remove credentials from local storage
        try {
            File(KeepTime.context.filesDir, "CredentialsFile").delete()
        } catch (e: java.io.IOException) { }
    }
}
