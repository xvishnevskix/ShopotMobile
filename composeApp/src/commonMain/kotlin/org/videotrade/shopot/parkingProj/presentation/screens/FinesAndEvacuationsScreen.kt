package videotrade.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import videotrade.parkingProj.presentation.components.Common.Common.CustomText
import videotrade.parkingProj.presentation.components.Common.Common.FontStyleType
import videotrade.parkingProj.presentation.components.Common.Common.TextType
import videotrade.parkingProj.presentation.components.Common.Title


class FinesAndEvacuationsScreen : Screen {

    @Composable
    override fun Content() {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader("Штрафы и эвакуации")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    ,
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                CustomText(
                    text = "Показать оплаченные и аннулированные",
                    type = TextType.BLUE,
                    fontStyle = FontStyleType.Medium,
                    modifier = Modifier
                    .clickable {  }
                    .padding(vertical = 8.dp, horizontal = 14.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Title("ЭВАКУАЦИИ")

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier.background(Color.White).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomText(
                        text = "Эвакуации не найдены",
                        fontStyle = FontStyleType.Medium,
                        type = TextType.SECONDARY_DARK,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomText(
                        text = "Обновить",
                        type = TextType.BLUE,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {  }
                        ,
                        fontStyle = FontStyleType.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomText(
                    text = "Эвакуация проверяется для автомобилей, добавленных в учетную запись.",
                    type = TextType.SECONDARY,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Title("ШТРАФЫ")

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier.background(Color.White).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomText(
                        text = "Эвакуации не найдены",
                        fontStyle = FontStyleType.Medium,
                        type = TextType.SECONDARY_DARK,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomText(
                        text = "Обновить",
                        type = TextType.BLUE,
                        modifier = Modifier.clickable {  }.align(Alignment.CenterHorizontally),
                        fontStyle = FontStyleType.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Title("ШТРАФЫ")

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier.background(Color.White).height(48.dp).fillMaxWidth().clickable {  }.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomText(
                        text = "Добавить документ",
                        type = TextType.BLUE,
                        fontStyle = FontStyleType.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                CustomText(
                    text = "По номеру свидетельства о регистрации ТС мы найдём штрафы за нарушения, которые зафиксировали камеры.",
                    type = TextType.SECONDARY,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomText(
                    text = "А по номеру водительского удостоверения — штрафы ГИБДД от инспектора",
                    type = TextType.SECONDARY,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
}