package it.uniroma1.keeptime

import android.app.Application
import android.content.Context


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
}
