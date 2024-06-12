package org.videotrade.shopot.multiplatform


expect class BackgroundTaskManager {
    fun scheduleTask()
}

expect object BackgroundTaskManagerFactory {
    fun create(): BackgroundTaskManager
}