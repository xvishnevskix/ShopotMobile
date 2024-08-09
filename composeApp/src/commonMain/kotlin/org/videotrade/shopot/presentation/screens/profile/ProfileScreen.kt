package org.videotrade.shopot.presentation.screens.profile

import Avatar
import ProfileSettingsButton
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.ProfileComponents.ProfileHeader
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.screens.settings.LanguageScreen
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.edit_profile
import shopot.composeapp.generated.resources.exit_profile
import shopot.composeapp.generated.resources.language


data class ProfileSettingsItem(
    val drawableRes: DrawableResource,
    val size: Dp,
    val mainText: String,
//    val boxText: String,
    val onClick: () -> Unit
)

class ProfileScreen(
    private val profile: ProfileDTO? = null,
    private val anotherUser: Boolean,
    
    ) : Screen {
    
    @Composable
    override fun Content() {
        val mainViewModel: MainViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        
        val profile =
            if (profile !== null && anotherUser) profile else mainViewModel.profile.collectAsState(
                initial = ProfileDTO()
            ).value
        
        
        println("sssssss ${profile.id}")
        val mainScreenNavigator = commonViewModel.mainNavigator.collectAsState(initial = null).value
        
        val navigator = LocalNavigator.currentOrThrow
        val items = listOf(
            ProfileSettingsItem(
                Res.drawable.edit_profile,
                25.dp,
                stringResource(MokoRes.strings.edit_profile)
            ) {
                navigator.push(
                    ProfileEditScreen(profile)
                )
            },
//            ProfileSettingsItem(Res.drawable.carbon_media_library, 25.dp, "Медиа, ссылки и файлы") {
//                navigator.push(
//                    ProfileMediaScreen(profile)
//                )
//            },
//            ProfileSettingsItem(Res.drawable.theme, 25.dp, "Тема", {}),
//            ProfileSettingsItem(Res.drawable.wallpaper, 25.dp, "Обои", {}),
            
            ProfileSettingsItem(
                Res.drawable.language,
                25.dp,
                stringResource(MokoRes.strings.language)
            ) {
                navigator.push(
                    LanguageScreen()
                )
            },
            
            
            ProfileSettingsItem(
                Res.drawable.exit_profile,
                25.dp,
                stringResource(MokoRes.strings.log_out)
            ) {
                
                commonViewModel.mainNavigator.value?.let { mainViewModel.leaveApp(it) }
            },

//            ProfileSettingsItem(Res.drawable.black_star, 24.dp, "Закрепить сообщения"),
//            ProfileSettingsItem(Res.drawable.search_icon, 22.dp, "Поиск по чату"),
//            ProfileSettingsItem(Res.drawable.mute_icon, 18.dp, "Заглушить"),
//            ProfileSettingsItem(Res.drawable.signal, 18.dp, "Сигнал"),
//            ProfileSettingsItem(Res.drawable.download_photo, 19.dp, "Сохранить фото"),
        )
        
        
        
        Box(
            modifier = Modifier.fillMaxSize().background(
                color = Color(255, 255, 255)
            ),
            contentAlignment = Alignment.TopStart,
        ) {
            
            
            Column {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomEnd = 46.dp, bottomStart = 46.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(16.dp)
                ) {
                    ProfileHeader(stringResource(MokoRes.strings.info), false)
                    Avatar(
                        icon = profile.icon,
                        size = 186.dp
                    )
                    Text(
                        "${profile.firstName} ${profile.lastName}",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 9.dp),
                        color = Color(0xFF000000)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.padding(bottom = 15.dp),
                    ) {
                        Text(
                            profile.phone,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(end = if (profile.phone == "") 0.dp else 18.dp),
                            color = Color(0xFF979797)
                        )
                        profile.login?.let {
                            Text(
                                it,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 20.sp,
                                color = Color(0xFF979797)
                            )
                        }
                    }
                    
                    profile.description?.let {
                        Text(
                            it,
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF979797),
                            modifier = Modifier.padding(bottom = 15.dp),
                            
                            )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(width = 46.dp, height = 23.dp)) {
                        val path = Path().apply {
                            moveTo(x = 0f, y = 0f)
                            lineTo(x = size.width, y = 0f)
                            lineTo(x = size.width / 2, y = size.height)
                            close()
                        }
                        drawPath(path = path, color = Color(0xFFF3F4F6))
                    }
                }
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


//                    profile.description?.let {
//                        Text(
//                            it,
//                            textAlign = TextAlign.Center,
//                            fontSize = 14.sp,
//                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
//                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                            lineHeight = 20.sp,
//                            modifier = Modifier.padding(top = 10.dp),
//                            color = Color(0xFF000000)
//                        )
//                    }
//                    Text(
//                        "Июль, 2024",
//                        textAlign = TextAlign.Center,
//                        fontSize = 16.sp,
//                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        modifier = Modifier.padding(top = 5.dp),
//                        color = Color(0xFF979797)
//                    )
                }
                
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = 35.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(items) { item ->
                        ProfileSettingsButton(
                            drawableRes = item.drawableRes,
                            size = item.size,
                            mainText = item.mainText,
                            onClick = item.onClick
                        )
                    }
                    
                }
                
                
            }
//            BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
            
        }
        
    }
}



