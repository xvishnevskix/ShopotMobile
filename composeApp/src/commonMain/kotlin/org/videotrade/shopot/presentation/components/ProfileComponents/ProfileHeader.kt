package org.videotrade.shopot.presentation.components.ProfileComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Call.CallBar
import org.videotrade.shopot.presentation.components.Common.BackIcon
import org.videotrade.shopot.presentation.components.Common.ModalDialogWithoutText
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.screens.profile.ProfileEditScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.profile_edit
import shopot.composeapp.generated.resources.profile_exit

@Composable
fun ProfileHeader(text: String, commonViewModel: CommonViewModel = koinInject(), mainViewModel: MainViewModel = koinInject(), modalVisible: MutableState<Boolean>? = null
)
{
    val navigator = LocalNavigator.currentOrThrow


    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, )
                .background(Color(0xFFf9f9f9)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {


            Text(
                text = text,
                textAlign = TextAlign.Start,
                fontSize = 24.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                color = Color(0xFF373533),
                letterSpacing = TextUnit(0F, TextUnitType.Sp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.padding(start = 16.dp, end = 6.dp).clickable {
                        navigator.push(
                            ProfileEditScreen()
                        )
                    }
                ) {
                    Image(
                        modifier = Modifier.size(19.dp),
                        painter = painterResource(Res.drawable.profile_edit),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                    )
                }
                Box(
                    modifier = Modifier.padding(start = 15.dp, end = 2.dp).clickable {
                        commonViewModel.mainNavigator.value?.let {

                            if (modalVisible != null) {
                                modalVisible.value = true
                            }
//                            mainViewModel.leaveApp(it)
                        }
                    }
                ) {
                    Image(
                        modifier = Modifier.size(18.dp),
                        painter = painterResource(Res.drawable.profile_exit),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
//                    colorFilter = ColorFilter.tint(Color.Gray)
                    )
                }
            }
        }



        Box(Modifier.padding(bottom = 23.dp)) {
            CallBar()
        }
    }
    
}


