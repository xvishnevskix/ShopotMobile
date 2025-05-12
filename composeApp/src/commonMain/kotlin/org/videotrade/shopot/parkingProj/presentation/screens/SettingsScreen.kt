package videotrade.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import videotrade.parkingProj.presentation.components.Common.Common.CustomSwitchRow
import videotrade.parkingProj.presentation.components.Common.Title

class SettingsScreen : Screen {

    @Composable
    override fun Content() {
        val isChecked = remember{ mutableStateOf(false)}


        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader(text = "Настройки")


            LazyColumn() {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Title("Оповещать")
                    Spacer(modifier = Modifier.height(4.dp))
                    CustomSwitchRow(
                        checked = isChecked.value,
                        onCheckedChange = {},
                        title = "За 15 минут до конца оплаченного \n" +
                                "времени парковки",
                        isFirst = true,
                        isLast = false
                    )
                    CustomSwitchRow(
                        checked = isChecked.value,
                        onCheckedChange = {},
                        title = "В момент окончания парковки",
                        isFirst = false,
                        isLast = false
                    )
                    CustomSwitchRow(
                        checked = isChecked.value,
                        onCheckedChange = {},
                        title = "При удалении от места парковки",
                        isFirst = false,
                        isLast = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Title("Оповещать о штрафах по пуш")
                    Spacer(modifier = Modifier.height(4.dp))
                    CustomSwitchRow(
                        checked = isChecked.value,
                        onCheckedChange = {},
                        title = "За неоплату парковки",
                        isFirst = true,
                        isLast = false
                    )
                    CustomSwitchRow(
                        checked = isChecked.value,
                        onCheckedChange = {},
                        title = "Другие штрафы",
                        isFirst = false,
                        isLast = true
                    )


                    Spacer(modifier = Modifier.height(24.dp))
                    Title("Оповещать о штрафах по пуш")
                    Spacer(modifier = Modifier.height(4.dp))
                    CustomSwitchRow(
                        checked = isChecked.value,
                        onCheckedChange = {},
                        title = "По МСД",
                        isFirst = true,
                        isLast = true
                    )


                    Spacer(modifier = Modifier.height(24.dp))
                    Title("Показать на карте")
                    Spacer(modifier = Modifier.height(4.dp))
                    CustomSwitchRow(
                        checked = isChecked.value,
                        onCheckedChange = {},
                        title = "Парковки для автомобилей инвалидов",
                        isFirst = true,
                        isLast = false
                    )
                    CustomSwitchRow(
                        checked = isChecked.value,
                        onCheckedChange = {},
                        title = "Парковочные баллы",
                        isFirst = false,
                        isLast = false
                    )
                    CustomSwitchRow(
                        checked = isChecked.value,
                        onCheckedChange = {},
                        title = "Мои шлагбаумы",
                        isFirst = false,
                        isLast = true
                    )

                    Spacer(modifier = Modifier.height(64.dp))
                }

            }
        }
    }
}