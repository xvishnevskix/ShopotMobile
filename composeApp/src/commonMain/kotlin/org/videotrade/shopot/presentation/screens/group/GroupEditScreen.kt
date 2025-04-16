package org.videotrade.shopot.presentation.screens.group

import GroupUserCard
import ProfileSettingsButton
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.Common.getParticipantCountText
import org.videotrade.shopot.presentation.components.ProfileComponents.GroupProfileHeader
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.contacts.ContactsViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.add_photo
import shopot.composeapp.generated.resources.add_users


class GroupEditScreen(private val profile: ProfileDTO, private val chat: ChatItem) : Screen {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val viewModel: ChatViewModel = koinInject()
        val contactsViewModel: ContactsViewModel = koinInject()
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
                           .padding(horizontal = 16.dp)
                           .fillMaxWidth()
                           .background(colors.background)
                   ) {
                       GroupProfileHeader(stringResource(MokoRes.strings.edit), profile, chat, isEdit = true)
                       ProfileSettingsButton(drawableRes = Res.drawable.add_users,
                           width = 19.dp,
                           height = 15.dp,
                           stringResource(MokoRes.strings.add_members),
                           {
                               contactsViewModel.clearSelectedContacts()
                               
                               navigator.push(GroupAddMembersScreen(chat))
                           })

                       Spacer(modifier = Modifier.height(16.dp))

                       ProfileSettingsButton(drawableRes = Res.drawable.add_photo,
                           width = 22.dp,
                           height = 16.dp,
                           stringResource(MokoRes.strings.upload_photo),
                           {})

                   }

                   Spacer(modifier = Modifier.height(32.dp))

                   LazyColumn(
                       verticalArrangement = Arrangement.Top,
                       horizontalAlignment = Alignment.CenterHorizontally,
                       modifier = Modifier.weight(1f)
                   ) {

                       println("groupUsers: ${groupUsers}")
                       itemsIndexed(groupUsers) { _, groupUser ->

                           GroupUserCard(groupUser = groupUser, viewModel, isEdit = true, chat)
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


data class TabInfo(
    val title: String,
    val text: String
)