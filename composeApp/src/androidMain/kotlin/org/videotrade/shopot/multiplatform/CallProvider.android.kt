package org.videotrade.shopot.multiplatform

import android.app.ActivityOptions
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cafe.adriel.voyager.navigator.Navigator
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.AppActivity
import org.videotrade.shopot.R
import org.videotrade.shopot.androidSpecificApi.CallActionReceiver
import org.videotrade.shopot.androidSpecificApi.CallForegroundService
import org.videotrade.shopot.androidSpecificApi.getContextObj
import org.videotrade.shopot.presentation.screens.call.CallViewModel

actual class CallProvider(private val context: Context) {
    actual fun switchToSpeaker(switch: Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        
        // Устанавливаем режим связи
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        
        // Переключаем динамик
        audioManager.isSpeakerphoneOn = switch
    }
}

actual object CallProviderFactory {
    private lateinit var applicationContext: Context
    
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }
    
    actual fun create(): CallProvider {
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException("CallProviderFactory is not initialized")
        }
        return CallProvider(applicationContext)
    }
}

actual fun onResumeCallActivity(navigator: Navigator) {
    val activity = getContextObj.getActivity() as ComponentActivity
    
    // Регистрируем наблюдателя за жизненным циклом
    activity.lifecycle.addObserver(MyLifecycleObserver(navigator))
}


class MyLifecycleObserver(private val navigator: Navigator) : DefaultLifecycleObserver {
    
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        println("onResume был вызван")
        val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
        
        if (callViewModel.replaceInCall.value) {
            println("Pushh!!!!!")
            
            callViewModel.callScreenInfo.value?.let { navigator.push(it) }        // Ваш код, который нужно выполнить при onResume
        }
        
        
    }
}

@RequiresApi(Build.VERSION_CODES.O)
actual fun isCallActiveNatific() {
    val context = getContextObj.getContext()
    
    val serviceIntent = Intent(context, CallForegroundService::class.java)
    context.stopService(serviceIntent)
    
    // Закрытие уведомления с кнопками "Принять" и "Отклонить"
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(1) // ID уведомления, используемого для Foreground Service
    
    // Создание нового уведомления, которое будет висеть
    val channelId = "OngoingCallChannel"
    
    // PendingIntent для запуска активности при нажатии на уведомление
    val ongoingIntent = Intent(context, CallActionReceiver::class.java).apply {
        action = "ACTION_CLICK_ONGOING_NOTIFICATION"
    }
    val ongoingPendingIntent = PendingIntent.getBroadcast(
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
    
    notificationManager.notify(2, ongoingNotification) // Устанавливаем ID 2 для нового уведомления
}

actual fun clearNotificationsForChannel(channelId: String) {
    // Получаем контекст через getContextObj.getContext()
    val context = getContextObj.getContext()
    
    // Получаем NotificationManager из контекста
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    // Проверяем активные уведомления и удаляем те, которые соответствуют переданному channelId
    val activeNotifications = notificationManager.activeNotifications
    for (notification in activeNotifications) {
        println("notification $notification")
        if (notification.notification.channelId == channelId) {
            notificationManager.cancel(notification.id)  // Удаление уведомления по ID
        }
    }
}

actual fun closeAppAndCloseCall() {
}