package org.videotrade.shopot.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.parkingProj.presentation.components.Common.BaseHeader
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomInfoRow
import org.videotrade.shopot.parkingProj.presentation.components.Common.TextType
import org.videotrade.shopot.parkingProj.presentation.components.Common.Title

class NotificationsScreen : Screen {

    @Composable
    override fun Content() {

        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader(text = "Уведомления")


            Column {
                Spacer(modifier = Modifier.height(24.dp))
                Title("17 февраля 2025")
                Spacer(modifier = Modifier.height(4.dp))
                CustomInfoRow("Изменились правила проезда по МСД.\n" +
                        "Рекомендуем ознакомиться с нововведения ново",
                    onClick = {},
                    type = TextType.SECONDARY_DARK,
                    isFirst = false,
                    isLast = true,
                    maxLines = 2
                )
            }
        }
    }
}