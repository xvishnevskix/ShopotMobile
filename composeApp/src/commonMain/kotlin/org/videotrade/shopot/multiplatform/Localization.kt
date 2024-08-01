package org.videotrade.shopot.multiplatform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.http.ContentType
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import org.videotrade.shopot.presentation.screens.chats.ChatsScreen
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.presentation.tabs.ChatsTab
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Res


@Composable
fun LanguageSelector()  {
//    val tabNavigator: TabNavigator = LocalTabNavigator.current
    val storedLanguage = getValueInStorage("selected_language")

    when (storedLanguage) {
        "en" -> Text(
            "Продолжить на русском" ,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 15.sp,
            color = Color(0xFF000000),
            modifier =  Modifier.padding(top = 25.dp).clickable{
                delValueInStorage("selected_language")
                StringDesc.localeType = StringDesc.LocaleType.Custom("ru")
                addValueInStorage("selected_language", "ru")

            },
            textDecoration = TextDecoration.Underline
        )
        "ru" -> Text(
            "Continue in English" ,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 15.sp,
            color = Color(0xFF000000),
            modifier =  Modifier.padding(top = 25.dp).clickable{
                delValueInStorage("selected_language")
                StringDesc.localeType = StringDesc.LocaleType.Custom("en")
                addValueInStorage("selected_language", "en")
                //        commonViewModel.mainNavigator.value?.push(IntroScreen())
            },
            textDecoration = TextDecoration.Underline
        )
    }
//    Button(onClick = {
//        delValueInStorage("selected_language")
//        StringDesc.localeType = StringDesc.LocaleType.Custom("en")
//        addValueInStorage("selected_language", "en")
//        tabNavigator.current = ChatsTab
//    }) {
//        Text(text = "English")
//    }
//
//    Button(onClick = {
//        delValueInStorage("selected_language")
//        StringDesc.localeType = StringDesc.LocaleType.Custom("ru")
//        addValueInStorage("selected_language", "ru")
//        tabNavigator.current = ChatsTab
//    }) {
//        Text(text = "Русский")
//    }
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
