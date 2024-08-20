package org.videotrade.shopot.multiplatform

import android.app.NotificationManager
import android.content.Context
import org.videotrade.shopot.androidSpecificApi.getContextObj

actual  fun clearAllNotifications() {
    val context = getContextObj.getContext()
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancelAll()
}
