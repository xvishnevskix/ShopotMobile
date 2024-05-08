package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.randomUser

@Composable
fun UserComponentItem(chat: UserItem) {
    val navigator = LocalNavigator.currentOrThrow


    Row(
        modifier = Modifier.fillMaxWidth().clickable {
            navigator.push(ChatScreen(chat))
        },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(Res.drawable.randomUser),
                contentDescription = null,

                )

            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    "${chat.firstName} ${chat.lastName}",

                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold

                )

                Text(
                    chat.lastMessage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal

                )
            }
        }

        Row {
            Text(
                "12:40",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold

            )
        }


    }

}