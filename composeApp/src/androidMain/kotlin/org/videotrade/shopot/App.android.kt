package org.videotrade.shopot

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.core.context.startKoin
import org.videotrade.shopot.di.getSharedModules
import org.videotrade.shopot.multiplatform.ContactsProviderFactory
import org.videotrade.shopot.multiplatform.DeviceIdProviderFactory
import org.videotrade.shopot.multiplatform.MediaProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProvider
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import javax.annotation.Nullable


class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }
    
    override fun onCreate() {
        super.onCreate()
        initializeFactories(this)
        startKoin { modules(getSharedModules()) }
        INSTANCE = this
    }
    
    private fun initializeFactories(context: Context) {
        DeviceIdProviderFactory.initialize(context)
        ContactsProviderFactory.initialize(context)
    }
}

class AppActivity : ComponentActivity() {
    private lateinit var permissionsProvider: PermissionsProvider
    private var permissionResultCallback: ((Int, IntArray) -> Unit)? = null
    
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        initializeProviders()
        
        setContent {
            App()
        }
    }
    
    private fun initializeProviders() {
        MediaProviderFactory.initialize(this)
        PermissionsProviderFactory.initialize(this)
        permissionsProvider = PermissionsProviderFactory.create()
    }
    
    fun registerActivityResultCallback(callback: (Int, IntArray) -> Unit) {
        permissionResultCallback = callback
    }
    
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions as Array<String>, grantResults)
        permissionResultCallback?.invoke(requestCode, grantResults)
    }
}


