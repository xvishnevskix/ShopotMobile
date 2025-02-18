package org.videotrade.shopot.androidSpecificApi

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
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.view.Display
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.AppActivity
import org.videotrade.shopot.R
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.presentation.screens.call.CallViewModel

// MyFirebaseMessagingService
class MyFirebaseMessagingService : FirebaseMessagingService() {
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("AAAAAAAAAAAAAAA ${remoteMessage.data}")
        // Проверка и запрос исключения из оптимизации батареи
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
        
        // Выполняем действия на основе данных сообщения
        triggerActionBasedOnData(remoteMessage.data)
        
    }
    
    private fun triggerActionBasedOnData(data: Map<String, String>) {
        
        when (data["action"]) {
            "callBackground" -> {
                val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
                val callUseCase: CallUseCase = KoinPlatform.getKoin().get()
                val profileId = getValueInStorage("profileId")
                
                println("profileId $profileId")
                
                if (profileId != null) {
                    val callData = data["callData"]
                    
                    if (callData != null) {
                        try {
                            val parseCallData = Json.parseToJsonElement(callData).jsonObject
                            println("callData41412412 $callData")
                            callViewModel.setIsCallBackground(true)
                            callViewModel.setIsIncomingCall(true)
                            
                            val userId =
                                parseCallData.jsonObject["userId"]?.jsonPrimitive?.content
                            
                            println("user.id ${userId}")
                            
                            if (userId != null) {
                                callViewModel.setOtherUserId(userId)
                            }
                            callViewModel.connectionCallWs(profileId)
                            
                            // Пробуждаем экран и показываем активность через Foreground Service
                            val serviceIntent = Intent(this, CallForegroundService::class.java)
                            startForegroundService(serviceIntent)
                            
                            // Устанавливаем данные вызова в callViewModel
                            callViewModel.setAnswerData(parseCallData)
                        } catch (e: Exception) {
                            println("Ошибка парсинга callData: ${e.message}")
                        }
                    }
                }
                // Ваш код, например, инициирование звонка
            }
            
            "messageInChat" -> {
                val channelId = "MessageInChatChannel"
                
                val callData = data["messageData"]
                
                val parseCallData = callData?.let { Json.parseToJsonElement(it).jsonObject }
                println("callData41412412 $callData")
                
                
                val title = parseCallData?.jsonObject?.get("title")?.jsonPrimitive?.content
                val message = parseCallData?.jsonObject?.get("body")?.jsonPrimitive?.content
                
                println("title ${title} $message")
                
                val notificationManager = NotificationManagerCompat.from(this)
                if (notificationManager.areNotificationsEnabled()) {
                    // Создаем и отправляем уведомление
                    val notification = NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                    
                    notificationManager.notify(3, notification.build())
                    
                }
            }
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
        val isScreenOn =
            powerManager.isInteractive
        val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
        
        callViewModel.setIsScreenOn(isScreenOn)
        
        // Создаем Intent для FullscreenNotificationActivity
        val fullScreenIntent = Intent(this, AppActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Создаем уведомление
        val channelId = "CallNotificationChannel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Входящий звонок")
            .setContentText("Входящий вызов")
//            .setSmallIcon(R.drawable.home_black)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
        
        // Если экран выключен — используем FullScreenIntent
        if (!isScreenOn) {
            notificationBuilder.setFullScreenIntent(fullScreenPendingIntent, true)
        } else {
            
            println("isScreenOn3123 $isScreenOn")
            
            // Добавляем действия для принятия и отклонения вызова
            val acceptIntent = Intent(this, CallActionReceiver::class.java).apply {
                action = "ACTION_ACCEPT_CALL"
            }
            val acceptPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            
            val declineIntent = Intent(this, CallActionReceiver::class.java).apply {
                action = "ACTION_DECLINE_CALL"
            }
            val declinePendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
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
        val channel = NotificationChannel(
            channelId,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
        
        // Запускаем Foreground Service
        startForeground(1, notification) // Используйте один и тот же ID для уведомления
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Пробуждаем устройство
        wakeDevice()
        
        // Если экран выключен, запускаем FullscreenNotificationActivity
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn =
            powerManager.isInteractive
        
        if (!isScreenOn) {
            val activityIntent = Intent(this, AppActivity::class.java)
            activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val options = ActivityOptions.makeBasic()
            options.setLaunchDisplayId(Display.DEFAULT_DISPLAY)
            startActivity(activityIntent, options.toBundle())
        }
        
        return START_NOT_STICKY
    }
    
    private fun wakeDevice() {
        
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            
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
                    callViewModel.setIsIncomingCall(false)
                    
                    val serviceIntent = Intent(context, CallForegroundService::class.java)
                    context.stopService(serviceIntent)
                    
                    // Закрытие уведомления с кнопками "Принять" и "Отклонить"
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(1) // ID уведомления, используемого для Foreground Service
                    
                    // Запуск FullscreenNotificationActivity
                    val activityIntent = Intent(context, AppActivity::class.java)
                    activityIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val options = ActivityOptions.makeBasic()
                    options.setLaunchDisplayId(Display.DEFAULT_DISPLAY)
                    context.startActivity(activityIntent, options.toBundle())
                    
                    
                }
            }
            
            "ACTION_DECLINE_CALL" -> {
                println("Вызов отклонен")
                try {
                    callViewModel.rejectCall(callViewModel.getOtherUserId(), "00:00:00")
                    callViewModel.disconnectWs()
                    
                    // Остановка Foreground Service
                    val serviceIntent = Intent(context, CallForegroundService::class.java)
                    context.stopService(serviceIntent)
                    
                    // Закрытие уведомления
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(1) // Закрываем уведомление Foreground Service
                } catch (e: Exception) {
                
                }
            }
            
            "ACTION_END_CALL" -> {
                println("Звонок завершен")
                // Завершение активности
                try {
                    callViewModel.rejectCall(callViewModel.getOtherUserId(), "00:00:00")
                    
                    callViewModel.disconnectWs()
                    
                } catch (e: Exception) {
                
                }
//                val activityManager =
//                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//
//                activityManager.appTasks.forEach { task ->
//                    if (task.taskInfo.baseActivity?.className == AppActivity::class.java.name) {
//                        task.finishAndRemoveTask()
//                    }
//                }
//
//                callViewModel.rejectCallBackground("")
//
//                // Закрытие уведомления
//                val notificationManager =
//                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                notificationManager.cancel(2) // Закрываем уведомление с ID 2
            }
            
            "ACTION_CLICK_ONGOING_NOTIFICATION" -> {
                println("Нажали на уведомление! Действие: ACTION_CLICK_ONGOING_NOTIFICATION")
                callViewModel.replaceInCall.value = true
                
                // Создаем Intent для запуска активности (приложения) или восстановления текущей
                val activityIntent = Intent(context, AppActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                
                // Запускаем активную активность (если она уже существует)
                context.startActivity(activityIntent)
            }
        }
    }
}

