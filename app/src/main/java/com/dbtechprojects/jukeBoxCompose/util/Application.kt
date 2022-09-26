package com.dbtechprojects.jukeBoxCompose.util

import android.app.Application
import com.google.firebase.FirebaseApp

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
    }
}