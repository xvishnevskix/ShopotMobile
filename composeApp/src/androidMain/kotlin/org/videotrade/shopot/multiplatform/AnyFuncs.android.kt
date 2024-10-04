package org.videotrade.shopot.multiplatform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import org.videotrade.shopot.AppActivity
import org.videotrade.shopot.R
import org.videotrade.shopot.androidSpecificApi.MyBroadcastReceiver
import org.videotrade.shopot.androidSpecificApi.getContextObj

actual fun getPlatform(): String {
    return "Android"
}

@RequiresApi(Build.VERSION_CODES.P)
actual fun getBuildVersion(): Long {
    val context = getContextObj.getContext()
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName  // версия приложения
    return packageInfo.longVersionCode
}


actual fun startOutgoingCall() {
    TODO()
}


//@RequiresApi(Build.VERSION_CODES.O)
//actual fun simulateIncomingCall() {
//    val context = getContextObj.getContext()
//    val getActivity = getContextObj.getActivity()
//
//    if (!checkAndRequestPhoneNumbersPermission(getActivity)) {
//        return
//    }
//
//    val callManager = CallManager(context, getActivity)
//    if (!callManager.isPhoneAccountRegistered()) {
//        callManager.registerPhoneAccount()
//        return
//    }
//
//    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
//    val phoneAccountHandle = PhoneAccountHandle(
//        ComponentName(context, MockCallService::class.java), "MyMockPhoneAccount"
//    )
//
//    val extras = Bundle().apply {
//        putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
//        putString(TelecomManager.EXTRA_CALL_SUBJECT, "Mock Incoming Call from MyApp")
//    }
//
//    try {
//        telecomManager.addNewIncomingCall(phoneAccountHandle, extras)
//    } catch (e: SecurityException) {
//        e.printStackTrace()
//    }
//}


actual fun simulateIncomingCall() {
    val context = getContextObj.getContext()
    val channelId = "incoming_call_channel"
    val channelName = "Incoming Call"
    
    // Создание NotificationChannel для Android 8.0 и выше
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
    
    // Intent для принятия вызова, который запускает основное Activity приложения
    val acceptIntent = Intent(context, AppActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        action = "ACTION_ACCEPT_CALL"
    }

    val acceptPendingIntent = PendingIntent.getActivity(
        context,
        0,
        acceptIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Добавлен флаг FLAG_IMMUTABLE
    )
    
    // Intent для отклонения вызова (можно оставить как `BroadcastReceiver` для выполнения соответствующих действий)
    val declineIntent = Intent(context, MyBroadcastReceiver::class.java).apply {
        action = "ACTION_DECLINE_CALL"
    }
    val declinePendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        declineIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Добавлен флаг FLAG_IMMUTABLE
    )
    
    // Создание уведомления о входящем вызове
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.home_black) // Поставь свой значок
        .setContentTitle("Входящий звонок")
        .setContentText("Звонок от вашего приложения")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .addAction(R.drawable.home_black, "Принять", acceptPendingIntent) // Кнопка принятия запускает MainActivity
        .addAction(R.drawable.home_black, "Отклонить", declinePendingIntent)
        .setAutoCancel(true)
        .build()
    
    // Показ уведомления
    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Нужно запросить разрешение на показ уведомлений
            return
        }
        notify(1, notification)
    }
    
}

