package org.videotrade.shopot.multiplatform

import platform.UserNotifications.UNUserNotificationCenter


actual fun clearAllNotifications() {
    val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    notificationCenter.removeAllDeliveredNotifications()
    notificationCenter.removeAllPendingNotificationRequests()
}