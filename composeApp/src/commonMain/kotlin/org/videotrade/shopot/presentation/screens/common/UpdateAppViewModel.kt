package org.videotrade.shopot.presentation.screens.common

import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.AppVersion
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getBuildVersion
import org.videotrade.shopot.multiplatform.getPlatform


class UpdateAppViewModel : ViewModel(), KoinComponent {

    val description = MutableStateFlow("")
    val appVersion = MutableStateFlow("")

    suspend fun checkVersion(): Boolean {
        try {
            val getVersion = origin().get<AppVersion>("auth/checkVersion")
            println("getVersion $getVersion")
            if (getPlatform() == Platform.Android) {

                if (getVersion !== null) {
                    val buildVersion = getBuildVersion()
                    return if (getVersion.criticalAppVersion.toLong() > buildVersion) {
                        description.value = getVersion.description
                        appVersion.value = getVersion.appVersion

                        true
                    } else {
                        false
                    }
                }

                return false

            }


            return false
        } catch (e: Exception) {

            return false

        }

    }
}


