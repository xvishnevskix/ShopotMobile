package org.videotrade.shopot.presentation.components.Main

import Avatar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.SearchDto
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res

@Composable
fun SearchUserItem(
    user: SearchDto,
    onClick: () -> Unit,


    ) {

    val viewModel: ChatViewModel = koinInject()
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(colors.surface).fillMaxWidth()
            .clickable {
//                val contact = user.toContactDTO()
//                contactsViewModel.createChat(contact)
//                isSearching.value = false
//                searchQuery.value = ""
//                mainViewModel.clearGlobalResults()
                onClick()


            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {


            Avatar(
                icon = user.icon,
                size = 56.dp
            )


            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Top
            ) {

                    Text(
                        text = user.firstName + " " + user.lastName,
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        color = colors.primary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        maxLines = 1, // Ограничиваем одной строкой
                        overflow = TextOverflow.Ellipsis, // Устанавливаем многоточие
                        modifier = Modifier.widthIn(max = 160.dp)
                    )


                Spacer(modifier = Modifier.height(16.dp))

                user.login?.let {
                    Text(
                        text = it,
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                        fontWeight = FontWeight(400),
                        color = colors.secondary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        maxLines = 1, // Ограничиваем одной строкой
                        overflow = TextOverflow.Ellipsis, // Устанавливаем многоточие
                        modifier = Modifier.widthIn(max = 160.dp)
                    )
                }


            }

        }

    }
}