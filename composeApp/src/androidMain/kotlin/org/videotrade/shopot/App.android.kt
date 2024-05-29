package org.videotrade.shopot

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.core.context.startKoin
import org.videotrade.shopot.di.getSharedModules
import org.videotrade.shopot.multiplatform.ContactsProviderFactory
import org.videotrade.shopot.multiplatform.DeviceIdProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProvider
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }
    
    override fun onCreate() {
        super.onCreate()
        DeviceIdProviderFactory.initialize(this)
        ContactsProviderFactory.initialize(this)
        
        startKoin { modules(getSharedModules()) }
        
        INSTANCE = this
    }
}

class AppActivity : ComponentActivity() {
    private lateinit var permissionsProvider: PermissionsProvider
    private var permissionResultCallback: ((Int, IntArray) -> Unit)? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
        
        PermissionsProviderFactory.initialize(this)
        permissionsProvider = PermissionsProviderFactory.create()
    }
    
    fun registerActivityResultCallback(callback: (Int, IntArray) -> Unit) {
        permissionResultCallback = callback
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResultCallback?.invoke(requestCode, grantResults)
    }
}

//internal actual fun openUrl(url: String?) {
//    val uri = url?.let { Uri.parse(it) } ?: return
//    val intent = Intent().apply {
//        action = Intent.ACTION_VIEW
//        data = uri
//        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    }
//    AndroidApp.INSTANCE.startActivity(intent)
//}
