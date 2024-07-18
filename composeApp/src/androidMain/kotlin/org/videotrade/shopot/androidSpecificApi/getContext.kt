package org.videotrade.shopot.androidSpecificApi

import android.content.Context

object getContextObj {
    private lateinit var applicationContext: Context
    
    fun initialize(context: Context) {
        applicationContext = context
    }
    
    
    fun getContext(): Context {
        return applicationContext
    }
    
}