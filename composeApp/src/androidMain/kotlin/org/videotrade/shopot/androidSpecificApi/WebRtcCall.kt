package org.videotrade.shopot.androidSpecificApi

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.StatusHints
import android.telecom.TelecomManager
import android.view.WindowManager
import android.app.ActivityOptions
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.Text
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startForegroundService
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.App
import org.videotrade.shopot.AppActivity
import org.videotrade.shopot.R
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.multiplatform.simulateIncomingCall
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.call.IncomingCallScreen
import org.videotrade.shopot.presentation.screens.intro.IntroScreen

// MyFirebaseMessagingService
class MyFirebaseMessagingService : FirebaseMessagingService() {
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("AAAAAAAAAAAAAAA  ${remoteMessage.data}")
        
        // Проверка и запрос исключения из оптимизации батареи
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            println("asfafsaffaafa")
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                println("Запрос на исключение из оптимизации батареи отправлен")
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                println("Приложение уже исключено из оптимизации батареи")
            }

        }
        
        // Выполняем действия на основе данных сообщения
        triggerActionBasedOnData(remoteMessage.data)
        
        // Пробуждаем экран и показываем активность через Foreground Service
        val serviceIntent = Intent(this, CallForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("!!!!!")
            startForegroundService(serviceIntent)
        } else {
            println("......")
            startService(serviceIntent)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun triggerActionBasedOnData(data: Map<String, String>) {
        if (data["action"] == "callBackground") {
            println("Triggered action based on data")
        }
    }
}

// FullscreenNotificationActivity
class FullscreenNotificationActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        
        setContent {
            KoinContext {
                val callViewModel: CallViewModel = koinInject()
                
                val profileId = getValueInStorage("profileId")
                
                if (profileId != null) {
                    callViewModel.connectionBackgroundWs(profileId)
                }
                
                Navigator(
                    IncomingCallScreen("", ProfileDTO())
                ) { navigator ->
                    SlideTransition(navigator)
                }
            }
            
            
            // Добавляем флаги для пробуждения экрана и отображения активности поверх экрана блокировки
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
            
            println("FullscreenNotificationActivity created")
        }
    }
}

class CallForegroundService : Service() {
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Создаем Intent для FullscreenNotificationActivity
        val fullScreenIntent = Intent(this, FullscreenNotificationActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Создаем уведомление
        val channelId = "foreground_service_channel"
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Входящий звонок")
            .setContentText("Входящий вызов")
            .setSmallIcon(R.drawable.home_black)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Высокий приоритет
            .setCategory(NotificationCompat.CATEGORY_CALL) // Категория вызова
            .setFullScreenIntent(fullScreenPendingIntent, true) // FullScreenIntent
            .build()
        
        // Создаем канал уведомлений для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
        
        // Запускаем Foreground Service
        startForeground(1, notification)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("wakeDevice and startActivity")
        
        // Пробуждаем устройство
        wakeDevice()
        
        // Запускаем FullscreenNotificationActivity с использованием ActivityOptions
        val activityIntent = Intent(this, FullscreenNotificationActivity::class.java)
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val options = ActivityOptions.makeBasic()
        options.setLaunchDisplayId(Display.DEFAULT_DISPLAY)
        startActivity(activityIntent, options.toBundle())
        
        return START_NOT_STICKY
    }
    
    private fun wakeDevice() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
            "MyApp::WakeLock"
        )
        wakeLock.acquire(5000) // Держим WakeLock на 5 секунд для пробуждения устройства
    }
}

