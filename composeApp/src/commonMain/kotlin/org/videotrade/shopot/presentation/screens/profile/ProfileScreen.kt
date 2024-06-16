package org.videotrade.shopot.presentation.screens.profile

import Avatar
import ProfileSettingsButton
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.ProfileComponents.ProfileHeader
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.black_star
import shopot.composeapp.generated.resources.carbon_media_library
import shopot.composeapp.generated.resources.download_photo
import shopot.composeapp.generated.resources.mute_icon
import shopot.composeapp.generated.resources.search_icon
import shopot.composeapp.generated.resources.signal


data class ProfileSettingsItem(
    val drawableRes: DrawableResource,
    val size: Dp,
    val mainText: String,
    val boxText: String
)

class ProfileScreen : Screen {
    
    @Composable
    override fun Content() {
        val mainViewModel: MainViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        
        val profile = mainViewModel.profile.collectAsState(initial = ProfileDTO()).value
        val mainScreenNavigator = commonViewModel.mainNavigator.collectAsState(initial = null).value
        
        val navigator = LocalNavigator.currentOrThrow
//        val items = listOf(
////            ProfileSettingsItem(
////                Res.drawable.carbon_media_library,
////                22.dp,
////                "Медиа, ссылки и файлы",
////                "17"
////            ),
////            ProfileSettingsItem(Res.drawable.black_star, 24.dp, "Закрепить сообщения", "Нет"),
////            ProfileSettingsItem(Res.drawable.search_icon, 22.dp, "Поиск по чату", ""),
////            ProfileSettingsItem(Res.drawable.mute_icon, 18.dp, "Заглушить", "Нет"),
//            ProfileSettingsItem(Res.drawable.signal, 18.dp, "Сигнал", "Стандарт"),
////            ProfileSettingsItem(Res.drawable.download_photo, 19.dp, "Сохранить фото", "Стандарт"),
//        )
//
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            
            
            Column {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomEnd = 46.dp, bottomStart = 46.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(16.dp).clickable {
                            if (mainScreenNavigator != null) {
                                mainViewModel.leaveApp(mainScreenNavigator)
                            }
                        }
                ) {
                    ProfileHeader("Информация")
                    Avatar(
                        icon = null,
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
                        modifier = Modifier.padding(bottom = 24.dp),
                    ) {
                        Text(
                            profile.phone,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(end = 18.dp),
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
                }
//                Box(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Canvas(modifier = Modifier.size(width = 46.dp, height = 33.dp)) {
//                        val path = Path().apply {
//                            moveTo(x = 0f, y = 0f)
//                            lineTo(x = size.width, y = 0f)
//                            lineTo(x = size.width / 2, y = size.height)
//                            close()
//                        }
//                        drawPath(path = path, color = Color(0xFFF3F4F6))
//                    }
//                }
                
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

//                LazyColumn(
//                    modifier = Modifier
//                        .padding(top = 6.dp, bottom = 35.dp)
//                        .fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    items(items) { item ->
//                        ProfileSettingsButton(
//                            drawableRes = item.drawableRes,
//                            size = item.size,
//                            mainText = item.mainText,
//                            boxText = item.boxText
//                        )
//                    }
//
//                }
                
                
            }
//            BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
            
        }
        
    }
}



