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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
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
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.GroupUserRole
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.Call.CallBar
import org.videotrade.shopot.presentation.components.Common.BackIcon
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.group.GroupEditScreen
import org.videotrade.shopot.presentation.screens.group.GroupViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.setting_dots


@Composable
fun GroupProfileHeader(
    text: String,
    profile: ProfileDTO,
    chat: ChatItem,
    isEdit: Boolean,
) {
    val navigator = LocalNavigator.currentOrThrow
    val colors = MaterialTheme.colorScheme
    val groupViewModel: GroupViewModel = koinInject()
    val groupUserRole = groupViewModel.groupUserRole.collectAsState().value
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp).background(colors.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            
            ) {
            Box(
                modifier = Modifier.clip(CircleShape).padding(12.dp).clickable {
                    navigator.pop()
                },
            ) {
                BackIcon()
            }
            Text(
                text,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                color = colors.primary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
            )
            
            if (isEdit) {
                Box(
                    modifier = Modifier.clip(CircleShape).padding(12.dp).clickable {
                        navigator.push(GroupEditScreen(profile, chat))
                    })
                {
                    Image(
                        modifier = Modifier,
                        painter = painterResource(Res.drawable.setting_dots),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(colors.primary)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }
            
            
        }
        Box(Modifier.padding(bottom = 23.dp)) {
            CallBar()
        }
    }
    
}


