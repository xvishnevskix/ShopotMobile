package org.videotrade.shopot.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.parkingProj.presentation.components.Common.BaseHeader
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomInfoRow
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomText
import org.videotrade.shopot.parkingProj.presentation.components.Common.FontStyleType
import org.videotrade.shopot.parkingProj.presentation.components.Common.TextType

class PersonalInfoScreen : Screen {

    @Composable
    override fun Content() {
        val phone = "+7 (915) 010-12-70"

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader("Личные данные")
            Spacer(modifier = Modifier.height(4.dp))

            //кнопки
            Column(modifier = Modifier.background(Color(0xFFF1F1F1))) {
                CustomInfoRow(
                    leftText = "Указать ФИО",
                    rightText = null,
                    isLast = false,
                    isFirst = true,
                    onClick = { /* TODO */ },
                    type = TextType.BLUE
                )

                CustomInfoRow(
                    leftText = "Телефон",
                    rightText = phone,
                    isLast = true,
                    isFirst = false,
                    onClick = {},
                    type = TextType.PRIMARY,
                    rightTextType = TextType.SECONDARY,
                    showArrow = false,
                )

                CustomText(
                    text = "Вы можете изменить свой телефон в Личном кабинете на сайте.",
                    type = TextType.SECONDARY,
                    fontStyle = FontStyleType.Regular,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomInfoRow(
                    leftText = "Указать e-mail",
                    rightText = null,
                    isLast = true,
                    isFirst = true,
                    onClick = { /* TODO */ },
                    type = TextType.BLUE
                )

                CustomText(
                    text = "На электронную почту можно получать отчёты\nоб использовании платных правок",
                    type = TextType.SECONDARY,
                    fontStyle = FontStyleType.Regular,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 13.sp
                )


                Spacer(modifier = Modifier.height(12.dp))

                CustomInfoRow(
                    leftText = "Удалить учетную запись",
                    rightText = null,
                    isLast = false,
                    isFirst = true,
                    type = TextType.RED,
                    onClick = { /* TODO */ }
                )
                CustomInfoRow(
                    leftText = "Выйти",
                    rightText = null,
                    isLast = true,
                    isFirst = false,
                    type = TextType.PRIMARY,
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}
