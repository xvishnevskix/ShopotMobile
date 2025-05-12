package videotrade.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import videotrade.parkingProj.presentation.components.Common.Common.CustomInfoRow
import videotrade.parkingProj.presentation.components.Common.Common.CustomSwitchRow
import videotrade.parkingProj.presentation.components.Common.Common.CustomText
import videotrade.parkingProj.presentation.components.Common.Common.CustomTextInputRow
import videotrade.parkingProj.presentation.components.Common.Common.FontStyleType
import videotrade.parkingProj.presentation.components.Common.Common.TextType

class NewCarScreen : Screen {

    @Composable
    override fun Content() {
        var isForeignPlate by remember { mutableStateOf(false) }
        var isDefaultCar by remember { mutableStateOf(false) }
        var carName by remember { mutableStateOf("") }
        var stcValue by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("Легковой") }

        val carTypes = listOf("Легковой", "Грузовой", "Автобус", "Мотоцикл")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader("Новый автомобиль")

            Spacer(modifier = Modifier.height(32.dp))

            // Номер машины
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CustomText(
                    text = "A 000 AA 000",
                    type = TextType.SECONDARY,
                    fontStyle = FontStyleType.Medium,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))



            CustomSwitchRow(
                title = "Иностранный / специальный номер",
                checked = isForeignPlate,
                onCheckedChange = { isForeignPlate = it },
                isFirst = true
            )
            CustomTextInputRow(
                label = "Название",
                value = carName,
                placeholder = "Необязательно",
                onValueChange = { carName = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomSwitchRow(
                title = "Использовать по умолчанию",
                checked = isDefaultCar,
                onCheckedChange = { isDefaultCar = it },
                isLast = true
            )


            Spacer(modifier = Modifier.height(32.dp))


            CustomText(
                text = "ТИП АВТОМОБИЛЯ",
                type = TextType.SECONDARY,
                fontStyle = FontStyleType.Regular,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )


            carTypes.forEachIndexed { index, type ->
                CustomInfoRow(
                    leftText = type,
                    showCheckmark = selectedType == type,
                    isFirst = index == 0,
                    isLast = index == carTypes.lastIndex,
                    onClick = { selectedType = type }
                )
            }


            Spacer(modifier = Modifier.height(12.dp))

            // СТС
            CustomTextInputRow(
                label = "СТС",
                value = stcValue,
                placeholder = "Необязательно",
                onValueChange = { stcValue = it },
                isLast = true,
                isFirst = true
            )

            CustomText(
                text = "Укажите серию и номер свидетельства о регистрации ТС\nи узнавайте о штрафах",
                type = TextType.SECONDARY,
                fontStyle = FontStyleType.Regular,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}