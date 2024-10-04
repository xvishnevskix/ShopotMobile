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
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.StatusHints
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.AppActivity
import org.videotrade.shopot.R
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.multiplatform.simulateIncomingCall
import org.videotrade.shopot.presentation.screens.call.CallViewModel


//class MockCallService : ConnectionService() {
//
//    override fun onCreateIncomingConnection(
//        connectionManagerPhoneAccount: PhoneAccountHandle?,
//        request: ConnectionRequest?
//    ): Connection {
//        val connection = MockConnection()
//        connection.setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
//        connection.setCallerDisplayName("Mock Incoming Call", TelecomManager.PRESENTATION_ALLOWED)
//        connection.setRinging() // Устанавливаем состояние вызова как звонящий
//        // Событие при принятии вызова
//        connection.setConnectionAcceptedListener {
//            // Запуск активности вашего приложения
//            val intent = Intent(applicationContext, Activity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//        }
//
//        return connection
//    }
//}
//
//
//class MockConnection : Connection() {
//
//    private var connectionAcceptedListener: (() -> Unit)? = null
//
//    fun setConnectionAcceptedListener(listener: () -> Unit) {
//        connectionAcceptedListener = listener
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    override fun onAnswer() {
//        super.onAnswer()
//
//        val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
//
//        println("answerCallBackground")
//
//        callViewModel.answerCallBackground()
//
//        setActive()
//    }
//
//    override fun onReject() {
//        super.onReject()
//        // Логика для отклонения вызова
//        println("answerCallBackground")
//
//        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
//        destroy()
//    }
//
//    override fun onDisconnect() {
//        super.onDisconnect()
//        println("onDisconnect")
//
//        // Завершаем звонок
//        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
//        destroy()
//    }
//}
//
//
//class CallManager(private val context: Context,private val getActivity: Context? = null) {
//    private val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun registerPhoneAccount() {
//        val phoneAccountHandle = PhoneAccountHandle(
//            ComponentName(context, MockCallService::class.java),
//            "MyMockPhoneAccount"
//        )
//
//        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "Mock Call")
//            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER) // Используем только CAPABILITY_CALL_PROVIDER
//            .setHighlightColor(Color.BLUE)
//            .setShortDescription("Incoming Mock Call")
//            .setSupportedUriSchemes(listOf(PhoneAccount.SCHEME_TEL))
//            .build()
//
//        try {
//            telecomManager.registerPhoneAccount(phoneAccount)
//            println("PhoneAccount зарегистрирован успешно")
//
//            // Проверка на активацию
//            val phoneAccountList = telecomManager.callCapablePhoneAccounts
//            if (!phoneAccountList.contains(phoneAccountHandle)) {
//                // Если аккаунт не активен, направить пользователя в настройки
//                val intent = Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS)
//                getActivity?.startActivity(intent)
//                println("PhoneAccount не активен. Переход в настройки для активации.")
//            }
//        } catch (e: SecurityException) {
//            e.printStackTrace()
//            println("Ошибка при регистрации PhoneAccount: ${e.message}")
//        }
//    }
//
//
//
//    fun isPhoneAccountRegistered(): Boolean {
//        val phoneAccountHandle = PhoneAccountHandle(
//            ComponentName(context, MockCallService::class.java),
//            "MyMockPhoneAccount"
//        )
//        val phoneAccount = telecomManager.getPhoneAccount(phoneAccountHandle)
//        return phoneAccount != null && phoneAccount.isEnabled
//    }
//}
//
//
//@RequiresApi(Build.VERSION_CODES.O)
//fun checkAndRequestPhoneNumbersPermission(context: Context): Boolean {
//    val READ_PHONE_NUMBERS_REQUEST_CODE = 1001
//    val MANAGE_OWN_CALLS_REQUEST_CODE = 1002
//    val READ_PHONE_STATE_REQUEST_CODE = 1003
//
//    // Проверяем, что версия Android соответствует требованиям (READ_PHONE_NUMBERS доступно с Android O)
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//        Log.e("Permission", "READ_PHONE_NUMBERS permission is not supported on devices below Android O")
//        return false
//    }
//
//    // Проверяем, является ли контекст Activity
//    if (context is Activity) {
//        // Проверяем наличие разрешения на чтение номеров телефонов
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                context,
//                arrayOf(Manifest.permission.READ_PHONE_NUMBERS),
//                READ_PHONE_NUMBERS_REQUEST_CODE
//            )
//            return false
//        }
//
//        // Проверяем наличие разрешения на управление собственными звонками
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.MANAGE_OWN_CALLS)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                context,
//                arrayOf(Manifest.permission.MANAGE_OWN_CALLS),
//                MANAGE_OWN_CALLS_REQUEST_CODE
//            )
//            return false
//        }
//
//        // Проверяем наличие разрешения READ_PHONE_STATE
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                context,
//                arrayOf(Manifest.permission.READ_PHONE_STATE),
//                READ_PHONE_STATE_REQUEST_CODE
//            )
//            return false
//        }
//
//        // Все разрешения уже предоставлены
//        return true
//    } else {
//        // Выводим лог, если контекст не является Activity
//        Log.e("Permission", "Context is not an Activity, cannot request permissions")
//        return false
//    }
//}
//
//
//
// MyFirebaseMessagingService
class MyFirebaseMessagingService : FirebaseMessagingService() {
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("AAAAAAAAAAAAAAA")
        
        if (remoteMessage.data.isNotEmpty()) {
            triggerActionBasedOnData(remoteMessage.data)
        }
        
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }
    
    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel_id"
        
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun triggerActionBasedOnData(data: Map<String, String>) {
        if (data["action"] == "callBackground") {
            val callIntent = Intent(this, CallForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                println("adaffsaffafafa")
                simulateIncomingCall()
                startForegroundService(callIntent)
            } else {
                startService(callIntent)
            }
        }
    }
}

// MyCallService
class MyCallService : ConnectionService() {
    
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = MyConnection()
        connection.setInitializing()
        connection.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED)
        connection.setCallerDisplayName("Входящий звонок", TelecomManager.PRESENTATION_ALLOWED)
        
        val statusHints = StatusHints(
            "Звонок из приложения",
            Icon.createWithResource(this, R.drawable.home_black),
            Bundle()
        )
        connection.statusHints = statusHints
        
        connection.setActive()
        return connection
    }
    
    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = MyConnection()
        connection.setInitializing()
        connection.setCallerDisplayName("Исходящий вызов", TelecomManager.PRESENTATION_ALLOWED)
        connection.setDialing()
        return connection
    }
}

// MyConnection
class MyConnection : Connection() {
    
    override fun onAnswer() {
        setActive()
    }
    
    override fun onReject() {
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
    }
    
    override fun onDisconnect() {
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }
}

// Регистрация PhoneAccount
@RequiresApi(Build.VERSION_CODES.O)
fun registerSelfManagedPhoneAccount(context: Context) {
    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    val phoneAccountHandle = PhoneAccountHandle(
        ComponentName(context, MyCallService::class.java),
        "MySelfManagedPhoneAccount"
    )
    
    val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "My Call")
        .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
        .setHighlightColor(Color.BLUE)
        .setShortDescription("Incoming App Call")
        .setSupportedUriSchemes(listOf(PhoneAccount.SCHEME_TEL))
        .build()
    
    try {
        telecomManager.registerPhoneAccount(phoneAccount)
        println("PhoneAccount зарегистрирован успешно")
    } catch (e: SecurityException) {
        e.printStackTrace()
        println("Ошибка при регистрации PhoneAccount: ${e.message}")
    }
}

// CallForegroundService
class CallForegroundService : Service() {
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createIncomingCallNotification()
        return START_STICKY
    }
    
    private fun createIncomingCallNotification() {
        val channelId = "incoming_call_channel"
        val channelName = "Incoming Call"
        
        // Создание NotificationChannel для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = android.graphics.Color.RED
                enableVibration(true)
                setSound(null, null)
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        
        // Intent для открытия основного Activity при принятии звонка
        val fullScreenIntent = Intent(this, AppActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Intent для принятия вызова
        val acceptIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
            action = "ACTION_ACCEPT_CALL"
        }
        val acceptPendingIntent = PendingIntent.getBroadcast(
            this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Intent для отклонения вызова
        val declineIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
            action = "ACTION_DECLINE_CALL"
        }
        val declinePendingIntent = PendingIntent.getBroadcast(
            this, 0, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Создание уведомления
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.home_black) // Замените на вашу иконку
            .setContentTitle("Входящий звонок")
            .setContentText("Звонок от вашего приложения")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(fullScreenPendingIntent, true) // Полноэкранное уведомление
            .addAction(R.drawable.home_black, "Принять", acceptPendingIntent)
            .addAction(R.drawable.home_black, "Отклонить", declinePendingIntent)
            .setOngoing(true) // Уведомление остаётся до завершения вызова
            .build()
        
        // Запуск службы с уведомлением
        startForeground(1, notification)
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

// MyBroadcastReceiver
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACTION_ACCEPT_CALL" -> {
                val launchIntent =
                    context.packageManager.getLaunchIntentForPackage(context.packageName)
                launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                // Добавьте логику для начала соединения или звонка
            }
            "ACTION_DECLINE_CALL" -> {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(1)
            }
        }
    }
}
