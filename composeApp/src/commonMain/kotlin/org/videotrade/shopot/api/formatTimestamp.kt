package org.videotrade.shopot.api

import androidx.compose.runtime.Composable
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun formatTimestamp(timestamp: List<Int>): String {
    val year = timestamp[0]
    val month = timestamp[1]
    val day = timestamp[2]
    val hour = timestamp[3]
    val minute = timestamp[4]
    val second = timestamp[5]
    val nanosecond = timestamp[6]
    
    val localDateTime = LocalDateTime(year, month, day, hour, minute, second, nanosecond)
    val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    
    // Форматирование даты и времени
    val formattedDateTime = "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
    
    return formattedDateTime
}
