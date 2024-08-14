package org.videotrade.shopot.androidSpecificApi

import android.content.Context
import org.videotrade.shopot.AppActivity

object getContextObj {
    private lateinit var applicationContext: Context
    private lateinit var applicationActivity: AppActivity
    
    fun initializeContext(context: Context) {
        applicationContext = context
    }
    
    fun initializeActivity(appActivity: AppActivity) {
        applicationActivity = appActivity
    }
    
    
    fun getContext(): Context {
        return applicationContext
    }
    
    fun getActivity(): Context {
        return applicationActivity
    }
    
}