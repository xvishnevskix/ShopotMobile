package org.videotrade.shopot.presentation.screens.group

import GroupUserCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import shopot.composeapp.generated.resources.Res
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.Common.getParticipantCountText
import org.videotrade.shopot.presentation.components.Main.GroupAvatar
import org.videotrade.shopot.presentation.components.ProfileComponents.GroupProfileHeader
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular


class GroupProfileScreen(private val profile: ProfileDTO, private val chat: ChatItem) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val viewModel: ChatViewModel = koinInject()
        val groupUsers = viewModel.groupUsers.collectAsState().value
        val colors = MaterialTheme.colorScheme


        LaunchedEffect(Unit) {
            viewModel.loadGroupUsers(chat.chatId)
        }

        Box(
            modifier = Modifier.fillMaxSize(1f).background(colors.background),
            contentAlignment = Alignment.TopStart
        ) {

            Column {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .background(colors.background)
                ) {
                    GroupProfileHeader(stringResource(MokoRes.strings.members), profile, chat, isEdit = false)
                    GroupAvatar(users = groupUsers, size = 59.dp, shape = 16.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "${chat.groupName}",
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        color = colors.primary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        groupUsers.size.toString() + "  " + stringResource(MokoRes.strings.members),
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                        fontWeight = FontWeight(400),
                        color = colors.secondary ,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp).weight(1f)
                ) {

                    println("groupUsers: ${groupUsers}")
                    itemsIndexed(groupUsers) { _, groupUser ->

                        GroupUserCard(groupUser = groupUser, viewModel, isEdit = false, chat)
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
                        ).background(colors.background).padding(top = 28.dp),
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
    val colors = MaterialTheme.colorScheme
    Text(
        text = getParticipantCountText(count),
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
        fontWeight = FontWeight(500),
        color = colors.primary,
        letterSpacing = TextUnit(0F, TextUnitType.Sp)
        ,
        modifier = Modifier.padding(start = 12.dp)
    )
}
