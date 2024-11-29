package org.videotrade.shopot.presentation.screens.common

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.AppVersion
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.getBuildVersion


class UpdateAppViewModel : ViewModel(), KoinComponent {

    val description = MutableStateFlow("")
    val appVersion = MutableStateFlow("")

    suspend fun checkVersion(): Boolean {
        val getVersion = origin().get<AppVersion>("auth/checkVersion")

        if (getVersion !== null) {
            val buildVersion = getBuildVersion()
            return if (getVersion.criticalAppVersion.toLong() > buildVersion) {
                description.value = getVersion.description
                appVersion.value = getVersion.appVersion
                false
                
//                true
            } else {
                false
            }


        }

        return true
    }
}


