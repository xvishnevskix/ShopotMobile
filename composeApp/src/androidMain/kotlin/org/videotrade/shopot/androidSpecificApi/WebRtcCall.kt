package org.videotrade.shopot.androidSpecificApi

import android.app.ActivityManager
import android.app.ActivityOptions
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.view.Display
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationCompat
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.AppActivity
import org.videotrade.shopot.R
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel

// MyFirebaseMessagingService
class MyFirebaseMessagingService : FirebaseMessagingService() {
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("AAAAAAAAAAAAAAA ${remoteMessage.data}")
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
        
        
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun triggerActionBasedOnData(data: Map<String, String>) {
        if (data["action"] == "callBackground") {
            val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
            val profileId = getValueInStorage("profileId")
            
            println("profileId $profileId")
            
            if (profileId != null) {
                val callData = data["callData"]
                
                
                
                
                
                if (callData != null) {
                    try {
                        val parseCallData = Json.parseToJsonElement(callData).jsonObject
                        println("callData41412412 $callData")
                        
                        // Пробуждаем экран и показываем активность через Foreground Service
                        val serviceIntent = Intent(this, CallForegroundService::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(serviceIntent)
                        } else {
                            startService(serviceIntent)
                        }
                        
                        callViewModel.setIsCallBackground(true)
                        // Устанавливаем данные вызова в callViewModel
                        callViewModel.setAnswerData(parseCallData)
                    } catch (e: Exception) {
                        println("Ошибка парсинга callData: ${e.message}")
                    }
                }
            }
            // Ваш код, например, инициирование звонка
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
                val isConnectedWebrtc by callViewModel.isConnectedWebrtc.collectAsState()
                val isScreenOn by callViewModel.isScreenOn.collectAsState()
                
                val profileId = getValueInStorage("profileId")
                
                
                val answerData = callViewModel.answerData.value
                

                
                LaunchedEffect(Unit) {
                    if (profileId != null) {
                        
                        if (isScreenOn) {
                            callViewModel.initWebrtc()
                        }
                        
                        callViewModel.connectionBackgroundWs(profileId)
                    }
                }
                
                
                val userJson =
                    answerData?.jsonObject?.get("user")?.jsonObject
                
                
                val user =
                    Json.decodeFromString<ProfileDTO>(userJson.toString())
                println("user:421412 $user")
                
                
                val navScreen = if (isScreenOn) {
                    callViewModel.setIsCallBackground(true)
                    CallScreen(user.id, null, user.firstName, user.lastName, user.phone)
                } else {
                    callViewModel.setIsIncomingCall(true)
                    CallScreen(user.id, null, user.firstName, user.lastName, user.phone)
                }
                
                Navigator(
                    navScreen
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
        
        // Проверяем состояние экрана
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            powerManager.isInteractive
        } else {
            powerManager.isScreenOn
        }
        val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
        
        callViewModel.setIsScreenOn(isScreenOn)
        
        // Создаем Intent для FullscreenNotificationActivity
        val fullScreenIntent = Intent(this, FullscreenNotificationActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Создаем уведомление
        val channelId = "foreground_service_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Входящий звонок")
            .setContentText("Входящий вызов")
            .setSmallIcon(R.drawable.home_black)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
        
        // Если экран выключен — используем FullScreenIntent
        if (!isScreenOn) {
            notificationBuilder.setFullScreenIntent(fullScreenPendingIntent, true)
        } else {
            // Добавляем действия для принятия и отклонения вызова
            val acceptIntent = Intent(this, CallActionReceiver::class.java).apply {
                action = "ACTION_ACCEPT_CALL"
            }
            val acceptPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val declineIntent = Intent(this, CallActionReceiver::class.java).apply {
                action = "ACTION_DECLINE_CALL"
            }
            val declinePendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            notificationBuilder.addAction(
                R.drawable.accept_call_button, "Принять", acceptPendingIntent
            )
            notificationBuilder.addAction(
                R.drawable.decline_call_button, "Отклонить", declinePendingIntent
            )
        }
        
        
        val notification = notificationBuilder.build()
        
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
        startForeground(1, notification) // Используйте один и тот же ID для уведомления
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("wakeDevice and startActivity")
        
        // Пробуждаем устройство
        wakeDevice()
        
        // Если экран выключен, запускаем FullscreenNotificationActivity
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn =
            powerManager.isInteractive
        
        if (!isScreenOn) {
            val activityIntent = Intent(this, FullscreenNotificationActivity::class.java)
            activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val options = ActivityOptions.makeBasic()
            options.setLaunchDisplayId(Display.DEFAULT_DISPLAY)
            startActivity(activityIntent, options.toBundle())
        }
        
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

// BroadcastReceiver для обработки действий при входящем вызове
class CallActionReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        println("Вызов принят $intent")
        val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
        
        when (intent.action) {
            
            
            "ACTION_ACCEPT_CALL" -> {
                
                CoroutineScope(Dispatchers.IO).launch {
                    println("Вызов принят")
                    
                    val serviceIntent = Intent(context, CallForegroundService::class.java)
                    context.stopService(serviceIntent)
                    // Закрытие уведомления с кнопками "Принять" и "Отклонить"
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(1) // ID уведомления, используемого для Foreground Service
                    
                    // Запуск FullscreenNotificationActivity
                    val activityIntent = Intent(context, FullscreenNotificationActivity::class.java)
                    activityIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val options = ActivityOptions.makeBasic()
                    options.setLaunchDisplayId(Display.DEFAULT_DISPLAY)
                    context.startActivity(activityIntent, options.toBundle())
                    
                    // Создание нового уведомления, которое будет висеть
                    val channelId = "ongoing_call_channel"
                    
                    // PendingIntent для запуска активности при нажатии на уведомление
                    val ongoingIntent = Intent(context, FullscreenNotificationActivity::class.java)
                    ongoingIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val ongoingPendingIntent = PendingIntent.getActivity(
                        context, 0, ongoingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    
                    // Добавляем действие "Завершить" в уведомление
                    val endCallIntent = Intent(context, CallActionReceiver::class.java).apply {
                        action = "ACTION_END_CALL"
                    }
                    val endCallPendingIntent = PendingIntent.getBroadcast(
                        context,
                        1,
                        endCallIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    
                    val ongoingNotification = NotificationCompat.Builder(context, channelId)
                        .setContentTitle("Звонок в процессе")
                        .setContentText("Идет звонок")
                        .setSmallIcon(R.drawable.home_black)
                        .setPriority(NotificationCompat.PRIORITY_LOW) // Низкий приоритет, чтобы не мешать пользователю
                        .setOngoing(true) // Устанавливаем уведомление как постоянно отображаемое
                        .setContentIntent(ongoingPendingIntent) // Добавляем PendingIntent для клика по уведомлению
                        .addAction(
                            R.drawable.decline_call_button,
                            "Завершить",
                            endCallPendingIntent
                        ) // Кнопка "Завершить"
                        .build()
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            channelId,
                            "Ongoing Call Channel",
                            NotificationManager.IMPORTANCE_LOW
                        )
                        notificationManager.createNotificationChannel(channel)
                    }
                    
                    notificationManager.notify(
                        2,
                        ongoingNotification
                    ) // Устанавливаем ID 2 для нового уведомления
                }
                
                
            }
            
            "ACTION_DECLINE_CALL" -> {
                println("Вызов отклонен")
                
                // Остановка Foreground Service
                val serviceIntent = Intent(context, CallForegroundService::class.java)
                context.stopService(serviceIntent)
                
                // Закрытие уведомления
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(1) // Закрываем уведомление Foreground Service
            }
            
            "ACTION_END_CALL" -> {
                println("Звонок завершен")
                
                // Завершение активности
                val activityManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                
                activityManager.appTasks.forEach { task ->
                    if (task.taskInfo.baseActivity?.className == FullscreenNotificationActivity::class.java.name) {
                        task.finishAndRemoveTask()
                    }
                }
                
                val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
                
                callViewModel.rejectCallBackground("")
                // Закрытие уведомления
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(2) // Закрываем уведомление с ID 2
            }
        }
    }
}

