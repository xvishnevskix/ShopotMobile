package org.videotrade.shopot.presentation.screens.settings

import LanguageHeader
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.tabs.ChatsTab
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular


data class LanguageItemData(
    val language: String,
    val translation: String,
    val emoji: String,
    val langCode: String
)


class LanguageScreen : Screen {
    @Composable
    override fun Content() {


        val storedLanguage = getValueInStorage("selected_language")

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            SafeArea {
                LanguageHeader(stringResource(MokoRes.strings.language))

                val languages = listOf(
                    LanguageItemData("Русский", "Russian", "🇷🇺", "ru"),
                    LanguageItemData("English", "English", "\uD83C\uDDEC\uD83C\uDDE7", "en"),
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
                    verticalArrangement = Arrangement.Center
                ) {
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                    items(languages.size) { index ->
                        val (language, translation, emoji, letters) = languages[index]
                        val isSelected = when (storedLanguage) {
                            "ru" -> language == "Русский"
                            "en" -> language == "English"
                            // Дополните для других языков
                            else -> false
                        }
                        LanguageItem(
                            language = language,
                            translation = translation,
                            isSelected = isSelected,
                            emoji = emoji,
                            letters = letters,
                            isLastItem = index == languages.size - 1
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun LanguageItem(language: String, translation: String, emoji: String, isSelected: Boolean, isLastItem: Boolean, letters: String,) {
    val alpha = if (isSelected) 1f else 0.50f
    val tabNavigator: TabNavigator = LocalTabNavigator.current
    val navigator = LocalNavigator.currentOrThrow
    val commonViewModel: CommonViewModel = koinInject()
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 9.dp, start = 5.dp, end = 5.dp).clickable{
                    delValueInStorage("selected_language")
                    StringDesc.localeType = StringDesc.LocaleType.Custom(letters)
                    addValueInStorage("selected_language", letters)
//                    tabNavigator.current = ChatsTab
                    commonViewModel.restartApp()
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = emoji,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 25.dp).alpha(alpha)
                )
                Text(
                    text = language,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp,
                    color = Color(0xFF000000)
                )
            }
            Text(
                text = translation,
                textAlign = TextAlign.Center,
                fontSize = 19.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                modifier = Modifier,
                color = Color(0xFF979797)
            )
        }
        if (!isLastItem) {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(color = Color.Gray.copy(alpha = 0.42f), thickness = 1.dp, modifier = Modifier.fillMaxWidth(0.98f))
            }
        }
    }
}