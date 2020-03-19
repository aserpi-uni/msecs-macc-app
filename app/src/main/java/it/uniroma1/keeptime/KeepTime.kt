package it.uniroma1.keeptime

import android.app.Application
import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class KeepTime : Application() {
    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    companion object {
        var instance: KeepTime? = null
            private set

        val context: Context
            get() = instance!!
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(applicationContext)
    }
}
