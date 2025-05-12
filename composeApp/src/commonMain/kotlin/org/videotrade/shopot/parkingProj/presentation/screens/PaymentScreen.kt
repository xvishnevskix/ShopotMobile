package videotrade.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.DrawableResource
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.card_linked
import shopot.composeapp.generated.resources.new_card
import shopot.composeapp.generated.resources.phone
import shopot.composeapp.generated.resources.sbp
import videotrade.parkingProj.presentation.components.Common.Common.ButtonStyle
import videotrade.parkingProj.presentation.components.Common.Common.CustomButton
import videotrade.parkingProj.presentation.components.Common.Common.PaymentMethodCard

class PaymentScreen : Screen {

    @Composable
    override fun Content() {
        var selected by remember { mutableStateOf("card_5137") }
        val navigator = LocalNavigator.currentOrThrow

        val paymentMethods = listOf(
            PaymentMethod("Система быстрых платежей", "Комиссия: 0%", "sbp", Res.drawable.sbp, 20 to 27),
            PaymentMethod("Карта *5137", "Комиссия: 0%", "card_5137", Res.drawable.card_linked, 35 to 20),
            PaymentMethod("Банковская карта", "Без привязки. Комиссия: 0%", "bank_card", Res.drawable.new_card, 30 to 20),
            PaymentMethod("+7 (915) 010-12-70", "Комиссия: 0–5.6%", "phone", Res.drawable.phone, 23 to 35)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader("Способ оплаты")
            Spacer(modifier = Modifier.height(8.dp))
            paymentMethods.forEach { method ->
                PaymentMethodCard(
                    title = method.title,
                    subtitle = method.subtitle,
                    isSelected = selected == method.id,
                    onClick = {
                        selected = method.id
                        navigator.push(TopUpScreen())
                              },
                    icon = method.icon,
                    iconSize = method.iconSize,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                text = "Привязать банковскую карту",
                style = ButtonStyle.Link,
                onClick = { scope -> println("Привязка карты") }
            )
        }
    }
}

data class PaymentMethod(
    val title: String,
    val subtitle: String,
    val id: String,
    val icon: DrawableResource,
    val iconSize: Pair<Int, Int>
)

