package org.videotrade.shopot.presentation.screens.group

import Avatar
import GroupLongButton
import GroupUserCard
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Common.getParticipantCountText
import org.videotrade.shopot.presentation.components.ProfileComponents.GroupProfileHeader
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res


class GroupProfileScreen(private val profile: ProfileDTO, private val chat: ChatItem) : Screen {
    
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val viewModel: ChatViewModel = koinInject()
        val groupUsers = viewModel.groupUsers.collectAsState().value
        


//        LaunchedEffect(Unit) {
//            viewModel.loadGroupUsers(chat.chatId)
//        }

           Box(
               modifier = Modifier.fillMaxSize(1f).background(Color.White),
               contentAlignment = Alignment.TopStart
           ) {

               Column {
                   Column(
                       horizontalAlignment = Alignment.CenterHorizontally,
                       modifier = Modifier
                           .padding(horizontal = 24.dp)
                           .fillMaxWidth()
                           .background(Color.White)
                   ) {
                       GroupProfileHeader(stringResource(MokoRes.strings.edit))
//                    Avatar(
//                        icon = null,
//                        size = 116.dp
//                    )
//                    Text(
//                        "${chat.groupName}",
//                        textAlign = TextAlign.Center,
//                        fontSize = 20.sp,
//                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        modifier = Modifier.padding(top = 16.dp, bottom = 9.dp),
//                        color = Color(0xFF000000)
//                    )
//                    Text(
//                        groupUsers.size.toString() + "  " + stringResource(MokoRes.strings.members),
//                        textAlign = TextAlign.Center,
//                        fontSize = 16.sp,
//                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        color = Color(0xFF979797)
//                    )
                   }

                   Spacer(modifier = Modifier.height(32.dp))

                   LazyColumn(
                       verticalArrangement = Arrangement.Top,
                       horizontalAlignment = Alignment.CenterHorizontally,
                       modifier = Modifier.padding(horizontal = 16.dp).weight(1f)
                   ) {
                       itemsIndexed(groupUsers) { _, groupUser ->
                           GroupUserCard(true, groupUser)
                           Spacer(modifier = Modifier.height(16.dp))
                       }
                   }


                   Row(
                       modifier = Modifier
                           .padding(top = 5.dp)
                           .height(100.dp)
                           .fillMaxWidth()
                           .shadow(
                           elevation = 6.dp,
                           shape = RoundedCornerShape(8.dp),
                           clip = false,
                           ambientColor = Color.Gray,
                           spotColor = Color.Gray
                       ).background(Color.White).padding(top = 28.dp),
                       horizontalArrangement = Arrangement.Center
                   ) {
                       ParticipantCountText(groupUsers.size)
                   }
               }
           }
       }
    }


@Composable
private fun ParticipantCountText(count: Int) {
    Text(
        text = getParticipantCountText(count),
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
        fontWeight = FontWeight(500),
        color = Color(0xFF373533),
        letterSpacing = TextUnit(0F, TextUnitType.Sp),
    )
}


data class TabInfo(
    val title: String,
    val text: String
)