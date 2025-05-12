package org.videotrade.shopot.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.parkingProj.presentation.components.Common.BaseHeader
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomText
import org.videotrade.shopot.parkingProj.presentation.components.Common.FontStyleType
import org.videotrade.shopot.parkingProj.presentation.components.Common.TextType
import org.videotrade.shopot.parkingProj.presentation.components.Common.Title

class NewsScreen : Screen {
    
    @Composable
    override fun Content() {
        val newsList = listOf(
            NewsItem(
                "01 АПР. 2025",
                "Москва, 10:03:00",
                "В 2024 году почти 350 тыс. раз на территорию Северного речного вокзала заезжали на автомобилях",
                "Это на 12% больше, чем в 2023 году"
            ),
            NewsItem(
                "31 МАРТА 2025",
                "Москва, 12:02:00",
                "За 3 года количество парковочных мест для владельцев разрешений выросло более чем на 30%",
                "С конца 2022 года число мест для владельцев парковочных разрешений увеличилось с 2 тыс. до 2,6 тыс."
            ),
            NewsItem(
                "27 МАРТА 2025",
                "Москва, 12:02:00",
                "В Москве 11 парковок со шлагбаумом расположены вблизи театров",
                "Водители могут оставить авто на парковках со шлагбаумом на время посещения театра"
            ),
            NewsItem(
                "26 МАРТА 2025",
                "Москва, 11:03:00",
                "Напоминаем правила парковки для мотоциклов в Москве",
                "Уличные парковки в Москве бесплатные для мотоциклистов"
            ),
            NewsItem(
                "26 МАРТА 2025",
                "Москва, 11:03:00",
                "Напоминаем правила парковки для мотоциклов в Москве",
                "Уличные парковки в Москве бесплатные для мотоциклистов"
            ),
        )
        

            Column {
                BaseHeader("Новости")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFf1f1f1))

                ) {
                    var currentDate: String? = null
                    newsList.forEach { item ->
                        val showDateHeader = currentDate != item.date
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        if (showDateHeader) {
                            currentDate = item.date
                            item {

                                Title(item.date)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                        item {
                            NewsCard(item)
                            Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }

}


@Composable
fun NewsCard(item: NewsItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        CustomText(
            text = item.time,
            fontSize = 13.sp,
            type = TextType.SECONDARY,
            fontStyle = FontStyleType.Regular
        )

        Spacer(Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomText(
                text = item.title,
                fontSize = 16.sp,
                type = TextType.SECONDARY_DARK,
                fontStyle = FontStyleType.Medium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFC7C7CC) // такой же как в стрелках
            )
        }

        item.subtitle?.let {
            Spacer(Modifier.height(4.dp))
            CustomText(
                text = it,
                fontSize = 13.sp,
                type = TextType.SECONDARY,
                fontStyle = FontStyleType.Regular
            )
        }
    }
}



data class NewsItem(
    val date: String,
    val time: String,
    val title: String,
    val subtitle: String?
)

