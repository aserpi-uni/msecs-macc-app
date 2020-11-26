package it.uniroma1.keeptime.data

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.coroutines.resumeWithException

import it.uniroma1.keeptime.KeepTime
import it.uniroma1.keeptime.data.model.Worker
import it.uniroma1.keeptime.data.model.WorkerReference
import kotlinx.serialization.json.Json

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

object LoginRepository {
    // TODO: secure
    var authenticationToken: String? = null
        private set

    private val _user = MutableLiveData<WorkerReference>()
    val user: LiveData<WorkerReference> = _user

    private var server: String? = null

    /**
     * Asks the server for its Google OAuth
     * [client ID](https://developers.google.com/identity/sign-in/android/backend-auth#send-the-id-token-to-your-server).
     *
     * @see loginWithGoogle
     */
    suspend fun googleOauthId(server_: String): String {
        val serverBuilder = Uri.parse(if(server_.startsWith("https")) server_ else "https://$server_").buildUpon()
        server = serverBuilder.build().toString()
        serverBuilder.appendPath("google_oauth").appendPath("id")

        return suspendCancellableCoroutine { cont ->
            val request = StringRequest(
                Request.Method.GET, serverBuilder.build().toString(),
                Response.Listener { cont.resume(it) { } },
                Response.ErrorListener { cont.resumeWithException(it) }
            )

            KeepTime.instance.requestQueue.add(request)

            cont.invokeOnCancellation {
                request.cancel()
            }
        }
    }

    /**
     * Logs a user in using email address and password.
     */
    suspend fun loginWithEmail(email_: String, password: String, server_: String) {
        try {
            server = if (server_.startsWith("https")) server_ else "https://$server_"
            onLoginSuccess(authenticateWithEmail(email_, password))
        } catch (error: VolleyError) {
            onLoginFailure(error)
        }
    }

    /**
     * Sends the Google [idToken](https://developers.google.com/identity/sign-in/android/backend-auth)
     * retrieved by [LoginActivity][it.uniroma1.keeptime.LoginActivity] to the server
     * in order to get an authentication token.
     */
    suspend fun loginWithGoogle(idToken: String) {
        try {
            onLoginSuccess(authenticateWithGoogle(idToken))
        } catch (error: VolleyError) {
            onLoginFailure(error)
        }
    }

    /**
     * Login with stored credentials, if any and still valid.
     */
    suspend fun loginWithStoredCredentials(): Boolean {
        lateinit var savedCredentials: Triple<String, String, String>
        try {
            savedCredentials = retrieveCredentials()
        } catch (_: java.io.IOException) {
            return false
        }

        authenticationToken = savedCredentials.third
        _user.value = WorkerReference(savedCredentials.second, savedCredentials.first)
        server = Regex("(https:\\/\\/[^\\/]*)\\S*").matchEntire(savedCredentials.first)!!.groupValues[1]

        try {
            refreshUser()
            return true
        } catch(error: VolleyError) {
            onLoginFailure(error)
        }
    }

    /**
     * Logs a user out.
     */
    suspend fun logout() {
        val serverBuilder = Uri.parse(server).buildUpon()
        serverBuilder.appendPath("workers").appendPath("sign_out.json")

        suspendCancellableCoroutine<Unit> { continuation ->
            val logoutRequest = AuthenticatedJsonObjectRequest(
                Request.Method.DELETE, serverBuilder.build().toString(), null,
                Response.Listener { continuation.resume(Unit) { } },
                Response.ErrorListener { error -> continuation.resumeWithException(error) }
            )

            KeepTime.instance.requestQueue.add(logoutRequest)

            continuation.invokeOnCancellation {
                logoutRequest.cancel()
            }
        }
    }

    /**
     * Retrieves the logged user from the server.
     */
    suspend fun refreshUser() {
        _user.value = user.value!!.fromServer()
    }

    /**
     * Updates user information.
     */
    suspend fun updateUser(userParams: JSONObject) {
        val requestParams = JSONObject()
        requestParams.accumulate("worker", userParams)

        val response = suspendCancellableCoroutine<JSONObject> { cont ->
            val request = AuthenticatedJsonObjectRequest(
                Request.Method.PATCH,
                user.value!!.url,
                userParams,
                Response.Listener { cont.resume(it) { } },
                Response.ErrorListener { cont.resumeWithException(it) }
            )

            KeepTime.instance.requestQueue.add(request)

            cont.invokeOnCancellation {
                request.cancel()
            }
        }

        _user.value = Json.parse(Worker.serializer(), response.toString())

        if(userParams.has("email")) {
            File(KeepTime.context.filesDir, "CredentialsFile").delete()
            saveCredentials()
        }
    }

    /**
     * Removes stored credentials.
     */
     fun removeCredentials() {
        authenticationToken = null
        _user.value = null
        server = null

        // Remove credentials from local storage, if any
        try {
            File(KeepTime.context.filesDir, "CredentialsFile").delete()
        } catch (e: java.io.IOException) { }
    }

    private suspend fun authenticateWithEmail(email: String, password: String): JSONObject {
        val serverBuilder = Uri.parse(server).buildUpon()
        serverBuilder.appendPath("workers").appendPath("sign_in.json")

        val requestParameters = JSONObject("{\"worker\":{\"email\":\"$email\",\"password\":\"$password\"}}")
        return suspendCancellableCoroutine { cont ->
            val request = JsonObjectRequest(
                Request.Method.POST, serverBuilder.build().toString(), requestParameters,
                Response.Listener { cont.resume(it) { } },
                Response.ErrorListener { cont.resumeWithException(it) })

            KeepTime.instance.requestQueue.add(request)

            cont.invokeOnCancellation {
                request.cancel()
            }
        }
    }

    private suspend fun authenticateWithGoogle(idToken: String): JSONObject = suspendCancellableCoroutine { cont ->
        val request = JsonObjectRequest(
            Request.Method.POST,
            Uri.parse(server).buildUpon()
                .appendPath("google_oauth")
                .appendPath("sign_in.json")
                .build().toString(),
            JSONObject("{\"id_token\":\"$idToken\"}"),
            Response.Listener { cont.resume(it) { } },
            Response.ErrorListener { cont.resumeWithException(it) })

        KeepTime.instance.requestQueue.add(request)

        cont.invokeOnCancellation {
            request.cancel()
        }
    }

    private fun onLoginFailure(error: VolleyError): Nothing {
        removeCredentials()
        throw error
    }

    private suspend fun onLoginSuccess(response: JSONObject) {
        authenticationToken = response.getString("authentication_token")
        _user.value = WorkerReference(response.getString("email"), response.getString("url"))
        saveCredentials()

        refreshUser()
    }

    private suspend fun retrieveCredentials(): Triple<String, String, String> {
        val file = File(KeepTime.context.filesDir, "CredentialsFile")
        lateinit var encryptedFile: EncryptedFile
        lateinit var masterKeyAlias: String
        lateinit var authenticationToken: String
        lateinit var email: String
        lateinit var url: String

        withContext(Dispatchers.IO) {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            encryptedFile = EncryptedFile.Builder(
                file,
                KeepTime.context,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            encryptedFile.openFileInput().use { inStream ->
                ObjectInputStream(inStream).use {
                    url = it.readObject() as String
                    email = it.readObject() as String
                    authenticationToken = it.readObject() as String
                }
            }
        }

        return Triple(url, email, authenticationToken)
    }

    private fun saveCredentials() {
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
                outObjStream.writeObject(user.value!!.url.toString())
                outObjStream.writeObject(user.value!!.email)
                outObjStream.writeObject(authenticationToken!!)
            }
        }
    }
}
