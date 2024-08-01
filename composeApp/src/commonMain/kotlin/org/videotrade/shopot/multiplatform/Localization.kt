package org.videotrade.shopot.multiplatform

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.http.ContentType
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import org.videotrade.shopot.presentation.screens.chats.ChatsScreen
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.presentation.tabs.ChatsTab


@Composable
fun LanguageSelector(commonViewModel: CommonViewModel)  {
    val tabNavigator: TabNavigator = LocalTabNavigator.current


    Button(onClick = {
        delValueInStorage("selected_language")
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")
        addValueInStorage("selected_language", "en")
        tabNavigator.current = ChatsTab
    }) {
        Text(text = "English")
    }

    Button(onClick = {
        delValueInStorage("selected_language")
        StringDesc.localeType = StringDesc.LocaleType.Custom("ru")
        addValueInStorage("selected_language", "ru")
        tabNavigator.current = ChatsTab
    }) {
        Text(text = "Русский")
    }
}


@Composable
fun AppInitializer() {
    val storedLanguage = getValueInStorage("selected_language")
    
    println("storedLanguage ${StringDesc.LocaleType.System}")
    StringDesc.localeType = when (storedLanguage) {
        "en" -> StringDesc.LocaleType.Custom("en")
        "ru" -> StringDesc.LocaleType.Custom("ru")
        else -> StringDesc.LocaleType.System
    }
}
