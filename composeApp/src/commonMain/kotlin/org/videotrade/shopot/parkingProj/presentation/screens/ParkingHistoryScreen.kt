package videotrade.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import videotrade.parkingProj.presentation.components.Common.Common.CustomText
import videotrade.parkingProj.presentation.components.Common.Common.FontStyleType
import videotrade.parkingProj.presentation.components.Common.Common.TextType
import videotrade.parkingProj.presentation.components.Common.Title

data class HistoryEntry(
    val time: String,
    val title: String,
    val amount: String,
    val description: String
)

class ParkingHistoryScreen : Screen {

    @Composable
    override fun Content() {
        val activeTab = remember { mutableStateOf("Городские парковки") }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf1f1f1))
        ) {

            BaseHeader("История")

            Spacer(modifier = Modifier.height(16.dp))

            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HistoryTab("Городские парковки", activeTab.value == "Городские парковки") {
                        activeTab.value = "Городские парковки"
                    }
                    Spacer(Modifier.width(8.dp))
                    HistoryTab("Коммерческие парковки", activeTab.value == "Коммерческие парковки") {
                        activeTab.value = "Коммерческие парковки"
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    HistoryBlock(
                        date = "28 МАРТА 2025",
                        items = listOf(
                            HistoryEntry(
                                time = "20:53",
                                title = "Прерывание парковки",
                                amount = "+ 119,59 ₽",
                                description = "Досрочное прекращение, А 929 ВТ 50, зона 0309, Москва"
                            ),
                            HistoryEntry(
                                time = "20:25",
                                title = "Оплата парковки",
                                amount = "- 303,26 ₽",
                                description = "А 929 ВТ 50, зона 0309, 20:25–20:54 (Прекращена), Москва"
                            )
                        )
                    )

                    HistoryBlock(
                        date = "26 МАРТА 2025",
                        items = listOf(
                            HistoryEntry(
                                time = "11:50",
                                title = "Прерывание парковки",
                                amount = "+ 18,67 ₽",
                                description = "Досрочное прекращение, А 929 ВТ 50, зона 4267, Москва"
                            ),
                            HistoryEntry(
                                time = "11:04",
                                title = "Оплата парковки",
                                amount = "- 80,00 ₽",
                                description = "А 929 ВТ 50, зона 4267, 11:04–11:50 (Прекращена), Москва"
                            )
                        )
                    )
                }
            }

        }
    }
}

@Composable
fun HistoryTab(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier

            .background(if (selected) Color.White else Color(0xFFF2F2F7), RoundedCornerShape(6.dp))
            .height(80.dp)
            .width(180.dp)
            .clickable { onClick() }
            .border(
                width = 2.dp,
                color = if (selected) Color(0xFF65B144) else Color(0xFFE2E2E2),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center) {
            CustomText(
                text = text,
                fontStyle = FontStyleType.Bold,
                type = if (selected) TextType.PRIMARY else TextType.SECONDARY,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun HistoryItem(time: String, title: String, amount: String, description: String) {
    Row(
        modifier = Modifier
            .padding(top = 4.dp)
            .background(Color.White)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CustomText(text = time, type = TextType.SECONDARY, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CustomText(text = title)
                CustomText(
                    text = amount,
                    type = TextType.PRIMARY,
                    fontStyle = FontStyleType.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            CustomText(text = description, type = TextType.SECONDARY, fontSize = 13.sp)
//        Divider(color = Color(0xFFE2E2E2), thickness = 1.dp)
        }
    }
}

@Composable
fun HistoryBlock(date: String, items: List<HistoryEntry>) {
    Spacer(modifier = Modifier.height(32.dp))
    Title(date)
    items.forEach { entry ->
        HistoryItem(
            time = entry.time,
            title = entry.title,
            amount = entry.amount,
            description = entry.description
        )
    }
}
