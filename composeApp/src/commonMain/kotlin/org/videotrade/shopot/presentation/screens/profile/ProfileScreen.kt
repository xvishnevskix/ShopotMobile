package org.videotrade.shopot.presentation.screens.profile

import Avatar
import FAQ
import ProfileSettingsButton
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import getImageStorage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.Common.ModalDialogWithoutText
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.ProfileComponents.ProfileHeader
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.screens.settings.LanguageScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.edit_profile
import shopot.composeapp.generated.resources.language
import shopot.composeapp.generated.resources.log_out
import shopot.composeapp.generated.resources.profile_design
import shopot.composeapp.generated.resources.profile_language
import shopot.composeapp.generated.resources.support


data class ProfileSettingsItem(
    val drawableRes: DrawableResource,
    val with: Dp = 25.dp,
    val height: Dp = 25.dp,
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
        val profileViewModel: ProfileViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        val scope = rememberCoroutineScope()
        
        val profile = profileViewModel.profile.collectAsState().value
        
        val mainScreenNavigator = commonViewModel.mainNavigator.collectAsState(initial = null).value
        val imagePainter = getImageStorage(profile.icon, profile.icon, false)
        
        val navigator = LocalNavigator.currentOrThrow
        val items = listOf(
//            ProfileSettingsItem(
//                Res.drawable.profile_design,
//                20.dp,
//                20.dp,
//                stringResource(MokoRes.strings.edit_profile)
//            ) {
//                navigator.push(
//                    ProfileEditScreen()
//                )
//            },
            ProfileSettingsItem(
                Res.drawable.profile_language,
                18.dp,
                18.dp,
                stringResource(MokoRes.strings.language)
            ) {
                navigator.push(
                    LanguageScreen()
                )
            },

            ProfileSettingsItem(
                Res.drawable.support,
                18.dp,
                18.dp,
                stringResource(MokoRes.strings.support)
            ) {
                navigator.push(
                    FAQ()
                )
            },

        )

        val modalVisible = remember { mutableStateOf(false) }


        if (modalVisible.value) {
            ModalDialogWithoutText(
                onDismiss = { modalVisible.value = false },
                onConfirm = {

                    modalVisible.value = false
                    commonViewModel.mainNavigator.value?.let {
                        mainViewModel.leaveApp(it)
                    }

                },
                confirmText = stringResource(MokoRes.strings.log_out),
                dismissText = stringResource(MokoRes.strings.cancel),
                title = stringResource(MokoRes.strings.are_you_sure_you_want_to_go_out)
            )
        }
        SafeArea(backgroundColor = Color(0xFFf9f9f9)) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFf9f9f9)),
                contentAlignment = Alignment.TopStart,
            ) {


                Column {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ProfileHeader(
                            stringResource(MokoRes.strings.profile),
                            commonViewModel,
                            mainViewModel,
                            modalVisible)
                        Avatar(
                            icon = profile.icon,
                            size = 128.dp,
                            onClick = {
                                scope.launch {
                                    imagePainter.value?.let {
                                        commonViewModel.mainNavigator.value?.push(
                                            PhotoViewerScreen(
                                                imagePainter!!,
                                                messageSenderName = "${profile.firstName} ${profile.lastName}",
                                            )
                                        )
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "${profile.firstName} ${profile.lastName}",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF373533),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            modifier = Modifier,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier,
                        ) {
                            Text(
                                profile.phone,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = Color(0x80373533),
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                modifier = Modifier.padding(end = if (profile.phone == "") 0.dp else 1.dp),
                            )

                            Box(
                                modifier = Modifier.padding(start = 18.dp, end = 18.dp).clip(RoundedCornerShape(50.dp)).width(4.dp)
                                    .height(4.dp)
                                    .background(color = Color(0x80373533)),
                                contentAlignment = Alignment.Center
                            ) {

                            }

                            profile.login?.let {
                                Text(
                                    it,
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0x80373533),
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        profile.description?.let {
                            Text(
                                it,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                fontWeight = FontWeight(500),
                                color = Color(0xFFCAB7A3),
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                )
                        }
                        Spacer(modifier = Modifier.height(56.dp))
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
                                width = item.with,
                                height = item.height,
                                mainText = item.mainText,
                                onClick = item.onClick
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

        }
    }
}



