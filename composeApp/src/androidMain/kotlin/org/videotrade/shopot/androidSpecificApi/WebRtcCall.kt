package org.videotrade.shopot.androidSpecificApi

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.SessionDescriptionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.findContactByPhone
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.simulateIncomingCall
import org.videotrade.shopot.presentation.screens.call.CallViewModel

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onAnswer() {
        super.onAnswer()

        val callViewModel: CallViewModel = KoinPlatform.getKoin().get()

        println("answerCallBackground")

        callViewModel.answerCallBackground()

        setActive()
    }

    override fun onDisconnect() {
        super.onDisconnect()
        println("onDisconnect")

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


            val profileId = getValueInStorage("profileId")

            println("profileId $profileId")

            if (profileId != null) {
                val callData = data["callData"]

                val parseCallData = callData?.let { Json.parseToJsonElement(it) }

                if (parseCallData !== null) {

                    println("callData41412412 $callData")
                        callViewModel.setAnswerData(parseCallData)
                        callViewModel.initWebrtc()
                        callViewModel.connectionBackgroundWs(profileId)
                }
            }
            // Ваш код, например, инициирование звонка
        }
    }
}

