package org.videotrade.shopot.presentation.screens.settings

import LanguageHeader
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.tabs.ChatsTab
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
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
    @OptIn(InternalResourceApi::class)
    @Composable
    override fun Content() {

        val colors = MaterialTheme.colorScheme
        val storedLanguage = getValueInStorage("selected_language")

        SafeArea(backgroundColor = colors.background) {
            Column(
                modifier = Modifier.fillMaxWidth().background(colors.background)
            ) {

                LanguageHeader(stringResource(MokoRes.strings.select_language))

                val languages = listOf(
                    LanguageItemData("Русский", "Russian", "🇷🇺", "ru"),
                    LanguageItemData("English", "English", "\uD83C\uDDEC\uD83C\uDDE7", "en"),
                )






                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = stringResource(MokoRes.strings.selected),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        color = colors.primary
                    ),



                    )

                Spacer(modifier = Modifier.height(16.dp))

                val selectedLanguage = languages.find { it.langCode == storedLanguage }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(color = colors.onBackground, shape = RoundedCornerShape(size = 16.dp))

                ) {
                    println("storedLanguage ${storedLanguage}")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (selectedLanguage != null) {
                                Text(
                                    text = selectedLanguage.emoji,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(end = 10.dp),
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    color = colors.primary
                                )
                            } else {
                                Text(
                                    text = languages[0].emoji,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(end = 10.dp),
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    color = colors.primary
                                )
                            }
                            if (selectedLanguage != null) {
                                Text(
                                    text = selectedLanguage.language,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        color = colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    )
                                )
                            } else {
                                Text(
                                    text = languages[0].language,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        color = colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    )
                                )
                            }
                        }
                        if (selectedLanguage != null) {
                            Text(
                                text = selectedLanguage.translation,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                    fontWeight = FontWeight(400),
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    color = colors.primary
                                )
                            )
                        } else {
                            Text(
                                text = languages[0].translation,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                    fontWeight = FontWeight(400),
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    color = colors.primary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = stringResource(MokoRes.strings.other_languages),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        color = colors.primary
                    ),

                    )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
                    verticalArrangement = Arrangement.Top
                ) {
                    items(languages.size) { index ->
                        val (language, translation, emoji, letters) = languages[index]
                        val isSelected = storedLanguage == letters

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
fun LanguageItem(
    language: String,
    translation: String,
    emoji: String,
    isSelected: Boolean,
    isLastItem: Boolean,
    letters: String
) {
    val colors = MaterialTheme.colorScheme
    val backgroundColor = Color.Transparent
    val borderColor = if (isSelected) colors.primary else colors.secondary
    val textColor = if (isSelected) colors.primary else colors.secondary
    val tabNavigator: TabNavigator = LocalTabNavigator.current
    val commonViewModel: CommonViewModel = koinInject()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(size = 16.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .clickable {
                delValueInStorage("selected_language")
                StringDesc.localeType = StringDesc.LocaleType.Custom(letters)
                addValueInStorage("selected_language", letters)
//                    tabNavigator.current = ChatsTab
                commonViewModel.restartApp()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = emoji,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 10.dp),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    color = colors.primary
                )
                Text(
                    text = language,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                        fontWeight = FontWeight(400),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        color = textColor
                    )
                )
            }
            Text(
                text = translation,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    color = textColor
                )
            )
        }
    }

    if (!isLastItem) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}