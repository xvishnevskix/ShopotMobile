package org.videotrade.shopot.androidSpecificApi

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.multiplatform.simulateIncomingCall
import org.videotrade.shopot.presentation.screens.call.CallViewModel


class MockCallService : ConnectionService() {
    
    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = MockConnection()
        connection.setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
        connection.setCallerDisplayName("Mock Incoming Call", TelecomManager.PRESENTATION_ALLOWED)
        connection.setRinging() // Устанавливаем состояние вызова как звонящий
        // Событие при принятии вызова
        connection.setConnectionAcceptedListener {
            // Запуск активности вашего приложения
            val intent = Intent(applicationContext, Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        
        return connection
    }
}


class MockConnection : Connection() {
    
    private var connectionAcceptedListener: (() -> Unit)? = null
    
    fun setConnectionAcceptedListener(listener: () -> Unit) {
        connectionAcceptedListener = listener
    }
    
    
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onAnswer() {
        super.onAnswer()

        val callViewModel: CallViewModel = KoinPlatform.getKoin().get()

        println("answerCallBackground")

        callViewModel.answerCallBackground()

        setActive()
    }
    
    override fun onReject() {
        super.onReject()
        // Логика для отклонения вызова
        println("answerCallBackground")
        
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
    }

    override fun onDisconnect() {
        super.onDisconnect()
        println("onDisconnect")

        // Завершаем звонок
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }
}


class CallManager(private val context: Context,private val getActivity: Context? = null) {
    private val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun registerPhoneAccount() {
        val phoneAccountHandle = PhoneAccountHandle(
            ComponentName(context, MockCallService::class.java),
            "MyMockPhoneAccount"
        )
        
        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "Mock Call")
            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER) // Используем только CAPABILITY_CALL_PROVIDER
            .setHighlightColor(Color.BLUE)
            .setShortDescription("Incoming Mock Call")
            .setSupportedUriSchemes(listOf(PhoneAccount.SCHEME_TEL))
            .build()
        
        try {
            telecomManager.registerPhoneAccount(phoneAccount)
            println("PhoneAccount зарегистрирован успешно")
            
            // Проверка на активацию
            val phoneAccountList = telecomManager.callCapablePhoneAccounts
            if (!phoneAccountList.contains(phoneAccountHandle)) {
                // Если аккаунт не активен, направить пользователя в настройки
                val intent = Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS)
                getActivity?.startActivity(intent)
                println("PhoneAccount не активен. Переход в настройки для активации.")
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            println("Ошибка при регистрации PhoneAccount: ${e.message}")
        }
    }



    fun isPhoneAccountRegistered(): Boolean {
        val phoneAccountHandle = PhoneAccountHandle(
            ComponentName(context, MockCallService::class.java),
            "MyMockPhoneAccount"
        )
        val phoneAccount = telecomManager.getPhoneAccount(phoneAccountHandle)
        return phoneAccount != null && phoneAccount.isEnabled
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun checkAndRequestPhoneNumbersPermission(context: Context): Boolean {
    val READ_PHONE_NUMBERS_REQUEST_CODE = 1001
    val MANAGE_OWN_CALLS_REQUEST_CODE = 1002
    val READ_PHONE_STATE_REQUEST_CODE = 1003

    // Проверяем, что версия Android соответствует требованиям (READ_PHONE_NUMBERS доступно с Android O)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        Log.e("Permission", "READ_PHONE_NUMBERS permission is not supported on devices below Android O")
        return false
    }

    // Проверяем, является ли контекст Activity
    if (context is Activity) {
        // Проверяем наличие разрешения на чтение номеров телефонов
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.READ_PHONE_NUMBERS),
                READ_PHONE_NUMBERS_REQUEST_CODE
            )
            return false
        }

        // Проверяем наличие разрешения на управление собственными звонками
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.MANAGE_OWN_CALLS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.MANAGE_OWN_CALLS),
                MANAGE_OWN_CALLS_REQUEST_CODE
            )
            return false
        }

        // Проверяем наличие разрешения READ_PHONE_STATE
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                READ_PHONE_STATE_REQUEST_CODE
            )
            return false
        }

        // Все разрешения уже предоставлены
        return true
    } else {
        // Выводим лог, если контекст не является Activity
        Log.e("Permission", "Context is not an Activity, cannot request permissions")
        return false
    }
}



class MyFirebaseMessagingService : FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
      println("AAAAAAAAAAAAAAA")

        // Обрабатываем данные сообщения
        if (remoteMessage.data.isNotEmpty()) {
            triggerActionBasedOnData(remoteMessage.data)
        }

        // Если сообщение содержит уведомление, создаём и показываем уведомление
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel_id" // Указываем ID канала, который был создан

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Приоритет для устройств ниже Android O
            .setAutoCancel(true) // Уведомление закрывается при нажатии

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build()) // Показать уведомление
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun triggerActionBasedOnData(data: Map<String, String>) {
        // Например, в зависимости от данных выполняем действия
        if (data["action"] == "callBackground") {
            val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
            val callUseCase: CallUseCase = KoinPlatform.getKoin().get()

            // Симулируем входящий вызов, например
            simulateIncomingCall()

            println("profileId")


//            val profileId = getValueInStorage("profileId")
//
//            println("profileId $profileId")
//
//            if (profileId != null) {
//                val callData = data["callData"]
//
//                val parseCallData = callData?.let { Json.parseToJsonElement(it) }
//
//                if (parseCallData !== null) {
//
//                    println("callData41412412 $callData")
//                        callViewModel.setAnswerData(parseCallData)
//                        callViewModel.initWebrtc()
//                        callViewModel.connectionBackgroundWs(profileId)
//                }
//            }
            // Ваш код, например, инициирование звонка
        }
    }
}

