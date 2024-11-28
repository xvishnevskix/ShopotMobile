package org.videotrade.shopot.presentation.screens.common

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.AppVersion
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.getBuildVersion


class UpdateAppViewModel : ViewModel(), KoinComponent {

    val profile = MutableStateFlow<ProfileDTO?>(null)


    suspend fun checkVersion(): Boolean {
        val getVersion = origin().get<AppVersion>("auth/checkVersion")

        if (getVersion !== null) {
            val buildVersion = getBuildVersion()

//            println("adasdada ${getVersion.criticalAppVersion.toLong() > buildVersion}")

            return getVersion.criticalAppVersion.toLong() > buildVersion
        }

        return true
    }
}


