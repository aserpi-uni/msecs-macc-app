package it.uniroma1.keeptime

import android.app.Application
import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Application class.
 *
 * @property requestQueue Volley queue used for all network operations
 */
class KeepTime : Application() {
    /**
     * @property context Application context
     * @property instance The unique instance of KeepTime
     */
    companion object {
        lateinit var instance: KeepTime
            private set

        val context: Context
            get() = instance
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(applicationContext)
    }
}
