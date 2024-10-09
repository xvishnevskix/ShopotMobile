package org.videotrade.shopot

//import org.videotrade.shopot.multiplatform.MediaProviderFactory
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import io.github.vinceglb.filekit.core.FileKit
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.videotrade.shopot.androidSpecificApi.getContextObj
import org.videotrade.shopot.di.getSharedModules
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.BackgroundTaskManagerFactory
import org.videotrade.shopot.multiplatform.CallProviderFactory
import org.videotrade.shopot.multiplatform.CipherInterface
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.ContactsProviderFactory
import org.videotrade.shopot.multiplatform.DeviceIdProviderFactory
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.NotificationHelper.createNotificationChannel
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.getAppLifecycleObserver


class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }
    
    internal fun provideEncapsulateChecker(cipherInterface: CipherInterface? = null): Module =
        module {
            single<CipherWrapper> { CipherWrapper(cipherInterface) }
        }
    
    override fun onCreate() {
        super.onCreate()
        
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        
        
        initializeFactories(this)
        startKoin {
            modules(getSharedModules() + provideEncapsulateChecker())
        }
        INSTANCE = this
        
        // Создаем канал уведомлений
        createNotificationChannel()
    }
    
    private fun initializeFactories(context: Context) {
        
        getContextObj.initializeContext(context)
        
        AudioFactory.initialize(context)
        FileProviderFactory.initialize(context)
        DeviceIdProviderFactory.initialize(context)
        ContactsProviderFactory.initialize(context)
        BackgroundTaskManagerFactory.initialize(context)
        CallProviderFactory.initialize(context)
        
    }
    
    // Метод для создания канала уведомлений
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel_id"
            val channel = NotificationChannel(
                channelId,
                "Incoming Call Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Канал для входящих вызовов"
                setShowBadge(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }


    }
}

open class AppActivity : ComponentActivity() {
    private var permissionResultCallback: ((Int, Boolean) -> Unit)? = null
    
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionResultCallback?.invoke(lastRequestCode, isGranted)
    }
    
    private var lastRequestCode: Int = -1
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getContextObj.initializeActivity(this)
        createNotificationChannel(this)
        
        FileKit.init(this)
        
        Firebase.initialize(this) // This line
        getAppLifecycleObserver()
        
        enableEdgeToEdge()
        initializeProviders()
        
        setContent {
            App()
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel_id"
            val channel = NotificationChannel(
                channelId,
                "Incoming Call Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Канал для входящих вызовов"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

    }
    
    private fun initializeProviders() {
        PermissionsProviderFactory.initialize(this)
        
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = dev.icerock.moko.mvvm.compose.R.drawable.notification_bg,
            )
        )
    }
    
    fun requestPermission(permission: String, requestCode: Int, callback: (Int, Boolean) -> Unit) {
        lastRequestCode = requestCode
        permissionResultCallback = callback
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(permission)
        } else {
            callback(requestCode, true)
        }
    }
    
    companion object {
        const val REQUEST_CODE_CAMERA = 1
        const val REQUEST_CODE_CONTACTS = 2
        const val REQUEST_CODE_MICROPHONE = 3
        const val REQUEST_CODE_NOTIFICATIONS = 4
    }
}
