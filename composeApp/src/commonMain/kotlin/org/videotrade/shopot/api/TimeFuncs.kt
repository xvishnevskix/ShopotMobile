package org.videotrade.shopot.api

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.videotrade.shopot.MokoRes

@Composable
fun formatTimestamp(timestamp: List<Int>): String {

    val yesterday = stringResource(MokoRes.strings.yesterday)

    return try {
        val year = timestamp[0]
        val month = timestamp[1]
        val day = timestamp[2]
        val hour = timestamp[3]
        val minute = timestamp[4]
        val second = timestamp[5]
        val nanosecond = timestamp[6]
        
        // Создание LocalDateTime на основе входящих данных
        val localDateTime = LocalDateTime(year, month, day, hour, minute, second, nanosecond)
        
        // Преобразование LocalDateTime в Instant в часовом поясе UTC (GMT+0)
        val instant = localDateTime.toInstant(TimeZone.UTC)
        
        // Получение текущего часового пояса системы
        val currentTimeZone = TimeZone.currentSystemDefault()
        
        // Преобразование Instant в LocalDateTime в текущем часовом поясе системы
        val dateTimeInCurrentZone = instant.toLocalDateTime(currentTimeZone)
        
        // Получение текущей даты
        val currentDate = Clock.System.now().toLocalDateTime(currentTimeZone).date
        
        // Сравнение даты
        when {
            dateTimeInCurrentZone.date == currentDate -> {
                // Сегодняшний день - выводим время
                "${
                    dateTimeInCurrentZone.hour.toString().padStart(2, '0')
                }:${dateTimeInCurrentZone.minute.toString().padStart(2, '0')}"
            }
            
            dateTimeInCurrentZone.date == currentDate.minus(1, DateTimeUnit.DAY) -> {
                // Вчера - выводим "вчера"
                yesterday + " ${
                    dateTimeInCurrentZone.hour.toString().padStart(2, '0')
                }:${dateTimeInCurrentZone.minute.toString().padStart(2, '0')}"
            }
            
            dateTimeInCurrentZone.date >= currentDate.minus(6, DateTimeUnit.DAY) -> {
                // Неделя назад - выводим день недели
                val dayOfWeek = dateTimeInCurrentZone.dayOfWeek.name.lowercase()
                    .replaceFirstChar { it.uppercase() }
                dayOfWeek
            }
            
            else -> {
                // Дата дальше недели назад - выводим дату в формате дд.ММ
                "${
                    dateTimeInCurrentZone.dayOfMonth.toString().padStart(2, '0')
                }.${dateTimeInCurrentZone.monthNumber.toString().padStart(2, '0')}"
            }
        }
    } catch (e: Exception) {
        ""
    }
}


fun getCurrentTimeList(): List<Int> {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return listOf(
        now.year,
        now.monthNumber,
        now.dayOfMonth,
        now.hour,
        now.minute,
        now.second,
        now.nanosecond
    )
}

fun formatDateOnly(dateOnly: List<Int>): String {
    return try {
        // Преобразуем список в дату

        val month = dateOnly[1]
        val day = dateOnly[2]

        // Форматируем дату в нужном виде, например, "13.09.2024"
        "${
            day.toString().padStart(2, '0')
        }.${month.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        ""
    }
}