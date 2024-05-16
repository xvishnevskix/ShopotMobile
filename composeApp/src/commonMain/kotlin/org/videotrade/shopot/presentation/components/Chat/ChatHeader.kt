package org.videotrade.shopot.presentation.components.Chat

import Avatar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Common.BackIcon
import org.jetbrains.compose.resources.Font
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.randomUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(chat: UserItem) {
    val interactionSource =
        remember { MutableInteractionSource() }  // Создаем источник взаимодействия
    val navigator = LocalNavigator.currentOrThrow
    


    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,

        ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {


            BackIcon(Modifier.padding(end = 8.dp).clickable {
                navigator.pop()
            })
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Avatar(
                drawableRes = Res.drawable.randomUser,
                size = 40.dp
            )

            Text(
                "${chat.firstName} ${chat.lastName}",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 16.dp),
            )
        }


        Box {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                modifier = Modifier.padding(end = 8.dp).size(20.dp).clickable {
                    navigator.pop()
                }
            )
        }

    }
}