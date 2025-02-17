package org.videotrade.shopot.presentation.components.Main

import Avatar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.videotrade.shopot.domain.model.GroupUserDTO

@Composable
fun GroupAvatar(users: List<GroupUserDTO>) {
    val avatars = users.take(4) // Берем первых 4 пользователей
    val avatarSize = 24.dp
    val spacing = 4.dp
    val cornerShape = 6.dp // Вынесенный roundedCornerShape

    Box(
        modifier = Modifier
            .size(56.dp)
    ) {
        when (avatars.size) {
            1 -> {
                Avatar(icon = avatars[0].icon, size = 56.dp, roundedCornerShape = cornerShape)
            }
            2 -> {
                Row(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    avatars.forEach { user ->
                        Avatar(icon = user.icon, size = avatarSize, roundedCornerShape = cornerShape)
                    }
                }
            }
            3 -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Avatar(icon = avatars[0].icon, size = avatarSize, roundedCornerShape = cornerShape)
                        Avatar(icon = avatars[1].icon, size = avatarSize, roundedCornerShape = cornerShape)
                    }
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Avatar(icon = avatars[2].icon, size = avatarSize, roundedCornerShape = cornerShape)
                    }
                }
            }
            4 -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Avatar(icon = avatars[0].icon, size = avatarSize, roundedCornerShape = cornerShape)
                        Avatar(icon = avatars[1].icon, size = avatarSize, roundedCornerShape = cornerShape)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Avatar(icon = avatars[2].icon, size = avatarSize, roundedCornerShape = cornerShape)
                        Avatar(icon = avatars[3].icon, size = avatarSize, roundedCornerShape = cornerShape)
                    }
                }
            }
        }
    }
}
