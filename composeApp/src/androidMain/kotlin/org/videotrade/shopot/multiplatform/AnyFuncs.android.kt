package org.videotrade.shopot.multiplatform

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import org.videotrade.shopot.androidSpecificApi.CallManager
import org.videotrade.shopot.androidSpecificApi.MockCallService
import org.videotrade.shopot.androidSpecificApi.checkAndRequestPhoneNumbersPermission
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
    val context = getContextObj.getContext()

    // Проверка разрешений перед использованием API
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
        try {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val phoneAccountHandle = PhoneAccountHandle(ComponentName(context, MockCallService::class.java), "MyMockPhoneAccount")

            val uri = Uri.fromParts("tel", "1234567890", null)  // Пример номера для теста
            val extras = Bundle().apply {
                putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
            }

            // Попытка сделать звонок
            telecomManager.placeCall(uri, extras)
        } catch (e: SecurityException) {
            // Обработка ситуации, если возникло исключение
            e.printStackTrace()
        }
    } else {

    }
}


@RequiresApi(Build.VERSION_CODES.O)
actual fun simulateIncomingCall() {
    val context = getContextObj.getContext()
    val getActivity = getContextObj.getActivity()

    // Проверяем разрешения с использованием Context
    if (!checkAndRequestPhoneNumbersPermission(getActivity)) {
        return
    }

    // Создаем инстанцию CallManager с использованием Context
    val callManager = CallManager(context, getActivity)


    // Проверяем регистрацию PhoneAccount
    if (!callManager.isPhoneAccountRegistered()) {
        callManager.registerPhoneAccount()


        // Показать уведомление или уведомить пользователя, чтобы он включил аккаунт позже
//        showNotificationToEnableAccount(context)
        return
    }

    // Если PhoneAccount зарегистрирован и активен, симулируем входящий звонок
    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    val phoneAccountHandle = PhoneAccountHandle(ComponentName(context, MockCallService::class.java), "MyMockPhoneAccount")

    val extras = Bundle().apply {
        putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
    }

    try {
        telecomManager.addNewIncomingCall(phoneAccountHandle, extras)
    } catch (e: SecurityException) {
        e.printStackTrace()
        // Обработка исключений
    }
}

