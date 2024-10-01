package org.videotrade.shopot.androidSpecificApi

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class WebRtcCallService : ConnectionService() {

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection? {
//        val connection = WebRtcConnection()
//        connection.setDialing()
//        // Настройте соединение WebRTC здесь
//        return connection
        return null
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection? {
//        val connection = WebRtcConnection()
//        connection.setDialing()
//        // Настройте исходящее соединение WebRTC здесь
//        return connection

        return null

    }
}


class WebRtcConnection : Connection() {

    override fun onAnswer() {
        super.onAnswer()
        // Начните WebRTC звонок, когда пользователь отвечает
    }

    override fun onDisconnect() {
        super.onDisconnect()
        // Завершите WebRTC звонок
    }
}


class MyPhoneAccountRegistration() {
    private val context = getContextObj.getContext()


    private val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    fun registerPhoneAccount() {
        val phoneAccountHandle = PhoneAccountHandle(
            ComponentName(context, WebRtcCallService::class.java),
            "MyWebRtcPhoneAccount"
        )
        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "WebRTC Calls")
            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
            .build()

        telecomManager.registerPhoneAccount(phoneAccount)
    }
}


fun CallO(

) {
    val context = getContextObj.getContext()

    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    val phoneAccountHandle = PhoneAccountHandle(
        ComponentName(context, WebRtcCallService::class.java),
        "MyWebRtcPhoneAccount"
    )

    val bundle = Bundle()
    telecomManager.addNewIncomingCall(phoneAccountHandle, bundle)

}


class MockCallService : ConnectionService() {

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = MockConnection()
        connection.setRinging()  // Имитация входящего вызова
        return connection
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = MockConnection()
        connection.setDialing()  // Имитация исходящего звонка
        return connection
    }
}


class MockConnection : Connection() {

    override fun onAnswer() {
        super.onAnswer()
        // Изменяем состояние на активный звонок
        setActive()
    }

    override fun onDisconnect() {
        super.onDisconnect()
        // Завершаем звонок
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }
}


class CallManager(private val context: Context) {
    private val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    fun registerPhoneAccount() {
        val phoneAccountHandle = PhoneAccountHandle(
            ComponentName(context, MockCallService::class.java),
            "MyMockPhoneAccount"
        )
        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "Mock Call")
            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
            .build()

        telecomManager.registerPhoneAccount(phoneAccount)
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

    // Проверяем наличие разрешения
    return if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_NUMBERS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Проверяем, является ли контекст Activity
        if (context is Activity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.READ_PHONE_NUMBERS),
                READ_PHONE_NUMBERS_REQUEST_CODE
            )
        } else {
            // Выводим лог или обрабатываем ситуацию, если context не является Activity
            Log.e("Permission", "Context is not an Activity, cannot request permissions")
        }
        false
    } else {
        // Разрешение уже предоставлено
        true
    }
}

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Проверяем, содержит ли сообщение данные
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")

            // Здесь вы можете вызвать нужный код на устройстве
            triggerActionBasedOnData(remoteMessage.data)
        }

        // Если сообщение содержит уведомление, вы можете также обработать его
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }
    }

    private fun triggerActionBasedOnData(data: Map<String, String>) {
        // Например, в зависимости от данных можно выполнить определённые действия
        if (data["action"] == "trigger_code") {
            // Выполняем нужный код
            Log.d("FCM", "Executing triggered code!")
            // Здесь вызовите ваш код
        }
    }
}

