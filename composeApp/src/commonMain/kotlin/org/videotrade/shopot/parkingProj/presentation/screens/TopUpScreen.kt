package videotrade.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import parkingproj.composeapp.generated.resources.Res
import parkingproj.composeapp.generated.resources.new_card
import videotrade.parkingProj.presentation.components.Common.Common.ButtonStyle
import videotrade.parkingProj.presentation.components.Common.Common.CustomButton
import videotrade.parkingProj.presentation.components.Common.Common.CustomText
import videotrade.parkingProj.presentation.components.Common.Common.FontStyleType
import videotrade.parkingProj.presentation.components.Common.Common.PaymentMethodCard
import videotrade.parkingProj.presentation.components.Common.Common.TextType
import videotrade.parkingProj.presentation.components.Common.CustomTextField

class TopUpScreen : Screen {
    @Composable
    override fun Content() {
        val selectedMethod = remember { mutableStateOf("card_5137") }
        var cash by remember { mutableStateOf("50 ₽") }
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader("Пополнить счёт")

            Spacer(Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(Modifier.padding(horizontal = 24.dp)
                , contentAlignment = Alignment.Center) {
                    CustomTextField(
                        value = cash,
                        onValueChange = { cash = it },
                        placeholder = "",
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                    )
                }


                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(3.dp)
                        .background(
                            color = Color(0x66000000),
                            shape = RoundedCornerShape(size = 2.dp)
                        )
                )

                Spacer(Modifier.height(8.dp))

                CustomText(
                    text = "Баланс после пополнения: 735,66 ₽",
                    type = TextType.SECONDARY,
                    fontSize = 14.sp,
                    fontStyle = FontStyleType.Regular
                )
            }

            Spacer(Modifier.height(32.dp))

            Column(
                Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomText(
                    text = "Способ оплаты",
                    type = TextType.SECONDARY,
                    fontSize = 12.sp,
                    fontStyle = FontStyleType.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp).align(Alignment.Start)
                )

                Spacer(Modifier.height(2.dp))

                PaymentMethodCard(
                    title = "Карта *5137",
                    subtitle = "Комиссия: 0%",
                    isSelected = selectedMethod.value == "card_5137",
                    onClick = {
//                        selectedMethod.value = "card_5137"
                        navigator.push(PaymentScreen())
                              },
                    icon = Res.drawable.new_card,
                    iconSize = 30 to 20
                )

                Spacer(Modifier.height(12.dp))

                CustomText(
                    text = "Обратите внимание на комиссию внешних сервисов",
                    type = TextType.PRIMARY,
                    fontSize = 14.sp,
                    fontStyle = FontStyleType.Regular,
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            CustomButton(
                text = "Оплатить 50 ₽",
                style = ButtonStyle.Primary,
                onClick = { scope -> println("Оплата прошла") },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}