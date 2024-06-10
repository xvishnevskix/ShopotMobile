package org.videotrade.shopot.api

import androidx.compose.runtime.Composable
import kotlinx.datetime.*

@Composable
fun formatTimestamp(timestamp: List<Int>): String {
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

    // Форматирование даты и времени
    val formattedDateTime = "${dateTimeInCurrentZone.hour.toString().padStart(2, '0')}:${dateTimeInCurrentZone.minute.toString().padStart(2, '0')}"

    return formattedDateTime
}
