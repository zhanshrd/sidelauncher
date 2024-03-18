package com.konstant.appmanager.utils

import android.app.Application

object ContextProvider {

    private lateinit var application: Application

    fun setApplication(application: Application) {
        this.application = application
    }

    fun getApplication(): Application {
        return application
    }

}