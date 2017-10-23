package com.example.filedownloader.myapplication

import android.app.Application
import android.util.Log
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException

/**
 * Created by kati4ka on 10/22/17.
 */

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler {
            if (it is IOException && it is SocketException && it is InterruptedException) {
                Log.w("App", "Network exception", it)
            } else if (it is NullPointerException || it is IllegalArgumentException || it is IllegalStateException) {
                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), it);
            } else {
                Log.w("App", "Undeliverable exception received, not sure what to do", it)
            }
        }
    }
}
