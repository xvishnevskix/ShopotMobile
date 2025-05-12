package videotrade.parkingProj.presentation.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import videotrade.parkingProj.presentation.components.Common.Common.CustomInfoRow
import videotrade.parkingProj.presentation.components.Common.Common.CustomText
import videotrade.parkingProj.presentation.components.Common.Common.FontStyleType
import videotrade.parkingProj.presentation.components.Common.Common.TextType

class MainSettingsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader(text = "Парковки")

            LazyColumn {
                item {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomInfoRow(
                            leftText = "+7 (915) 010-12-70",
                            rightText = "",
                            isFirst = true,
                            isLast = true,
                            onClick = {navigator.push(PersonalInfoScreen())},
                            type = TextType.SECONDARY
                        )

                        Spacer(modifier = Modifier.height(32.dp))


                        CustomInfoRow(
                            leftText = "Изменить завершенную парковку",
                            rightText = "0",
                            onClick = {},
                            isFirst = true,
                            isLast = true
                        )
                        CustomText(
                            text = "До 23:59 можно изменить номер авто и парковочной зоны,\nвремя начала или окончания парковки. Только для парковок Москвы",
                            type = TextType.SECONDARY,
                            fontStyle = FontStyleType.Regular,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            fontSize = 12.sp
                        )


                        Spacer(modifier = Modifier.height(16.dp))

                        // Баланс и баллы
                        CustomInfoRow(
                            leftText = "Пополнить счет",
                            rightText = "685,66 ₽",
                            onClick = {navigator.push(TopUpScreen())},
                            isFirst = true,
                        )
                        CustomInfoRow(
                            leftText = "Парковочные баллы",
                            rightText = "0 Б",
                            onClick = {},
                            isLast = true,
                            showArrow = false
                        )
                        CustomInfoRow(
                            leftText = "Аналитика расходов",
                            onClick = {},
                            isFirst = true,
                        )
                        CustomInfoRow(
                            leftText = "Уведомления",
                            onClick = {navigator.push(NotificationsScreen())},
                            isFirst = true,
                        )
                        CustomInfoRow(
                            leftText = "Новости",
                            onClick = {navigator.push(NewsScreen())},
                            isFirst = true,
                        )
                        CustomInfoRow(
                            leftText = "История",
                            onClick = {navigator.push(ParkingHistoryScreen())},
                            isFirst = true,
                            isLast = true
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        CustomInfoRow(
                            leftText = "Автомобили",
                            onClick = {navigator.push(CarsScreen())},
                            isFirst = true,
                            isLast = true
                        )
                        CustomText(
                            text = "Льготные парковочные разрешения, абонементы на парковку и привязка траспондера",
                            type = TextType.SECONDARY,
                            fontStyle = FontStyleType.Regular,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomInfoRow(
                            leftText = "ДТП",
                            onClick = {},
                            isFirst = true,
                            isLast = true
                        )
                        CustomText(
                            text = "Оформить европротокол онлайн",
                            type = TextType.SECONDARY,
                            fontStyle = FontStyleType.Regular,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomInfoRow(
                            leftText = "Проезды по платным дорогам",
                            onClick = {},
                            isFirst = true,
                            isLast = true
                        )
                        CustomText(
                            text = "Можно оплатить поездки по платным дорогам",
                            type = TextType.SECONDARY,
                            fontStyle = FontStyleType.Regular,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomInfoRow(
                            leftText = "Штрафы и эвакуации",
                            onClick = {navigator.push(FinesAndEvacuationsScreen())},
                            isFirst = true,
                            isLast = true
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        CustomInfoRow(
                            leftText = "Привязанные карты и автооплата",
                            onClick = {navigator.push(PaymentScreen())},
                            isFirst = true,
                            isLast = false
                        )
                        CustomInfoRow(
                            leftText = "Настройки",
                            onClick = {navigator.push(SettingsScreen())},
                            isFirst = false,
                            isLast = true
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        CustomInfoRow(
                            leftText = "Обратная связь",
                            onClick = {navigator.push(AppealScreen())},
                            isFirst = true,
                            isLast = false
                        )
                        CustomInfoRow(
                            leftText = "Чат-бот",
                            onClick = {},
                            isFirst = false,
                            isLast =  false
                        )
                        CustomInfoRow(
                            leftText = "Транспортные приложения",
                            onClick = {},
                            isFirst = false,
                            isLast = false
                        )
                        CustomInfoRow(
                            leftText = "О приложении",
                            onClick = {},
                            isFirst = false,
                            isLast = true
                        )

                        Spacer(modifier = Modifier.height(64.dp))

                    }
                }
            }
        }
    }
}