package org.videotrade.shopot.api

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.videotrade.shopot.MokoRes

@Composable
fun formatTimestamp(timestamp: List<Int>): String {

    val yesterday = stringResource(MokoRes.strings.yesterday)

    val monday = stringResource(MokoRes.strings.monday)
    val tuesday = stringResource(MokoRes.strings.tuesday)
    val wednesday = stringResource(MokoRes.strings.wednesday)
    val thursday = stringResource(MokoRes.strings.thursday)
    val friday = stringResource(MokoRes.strings.friday)
    val saturday = stringResource(MokoRes.strings.saturday)
    val sunday = stringResource(MokoRes.strings.sunday)

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


        fun getLocalizedDayOfWeek(dayOfWeek: DayOfWeek): String {
            return when (dayOfWeek) {
                DayOfWeek.MONDAY -> monday
                DayOfWeek.TUESDAY -> tuesday
                DayOfWeek.WEDNESDAY -> wednesday
                DayOfWeek.THURSDAY -> thursday
                DayOfWeek.FRIDAY -> friday
                DayOfWeek.SATURDAY -> saturday
                DayOfWeek.SUNDAY -> sunday
                else -> TODO()
            }
        }

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
                yesterday
//                + " ${
//                    dateTimeInCurrentZone.hour.toString().padStart(2, '0')
//                }:${dateTimeInCurrentZone.minute.toString().padStart(2, '0')}"
            }

            dateTimeInCurrentZone.date >= currentDate.minus(6, DateTimeUnit.DAY) -> {
                // Неделя назад - выводим день недели
                getLocalizedDayOfWeek(dateTimeInCurrentZone.dayOfWeek)
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

@Composable
fun formatDateOnly(dateOnly: List<Int>): String {
    // Получаем текущую дату и вчерашнюю дату
    val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentDate = currentDateTime.date
    val yesterdayDate = currentDate.minus(1, DateTimeUnit.DAY)

    // Создаем LocalDateTime на основе входящих данных
    val year = dateOnly[0]
    val month = dateOnly[1]
    val day = dateOnly[2]
    val localDate = LocalDateTime(year, month, day, 0, 0).date

    // Определяем день (сегодня, вчера или другая дата)
    return when {
        localDate == currentDate -> stringResource(MokoRes.strings.today)
        localDate == yesterdayDate -> stringResource(MokoRes.strings.yesterday)
        else -> {
            // Определяем название месяца
            val monthName = when (month) {
                1 -> stringResource(MokoRes.strings.january)
                2 -> stringResource(MokoRes.strings.february)
                3 -> stringResource(MokoRes.strings.march)
                4 -> stringResource(MokoRes.strings.april)
                5 -> stringResource(MokoRes.strings.may)
                6 -> stringResource(MokoRes.strings.june)
                7 -> stringResource(MokoRes.strings.july)
                8 -> stringResource(MokoRes.strings.august)
                9 -> stringResource(MokoRes.strings.september)
                10 -> stringResource(MokoRes.strings.october)
                11 -> stringResource(MokoRes.strings.november)
                12 -> stringResource(MokoRes.strings.december)
                else -> ""
            }
            "$day $monthName"
        }
    }
}

@Composable
fun formatTimeOnly(timestamp: List<Int>): String {
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

        println("localDateTime ${localDateTime}")

        // Преобразование LocalDateTime в Instant с предположением, что время в UTC
        val instant = localDateTime.toInstant(TimeZone.UTC)

        // Получение текущего часового пояса устройства
        val currentTimeZone = TimeZone.currentSystemDefault()

        println("currentTimeZone ${currentTimeZone}")

        // Преобразование Instant в LocalDateTime с учётом текущего часового пояса устройства
        val dateTimeInCurrentZone = instant.toLocalDateTime(currentTimeZone)

        println("dateTimeInCurrentZone ${dateTimeInCurrentZone}")

        // Форматирование времени с учётом локального времени устройства
        "${dateTimeInCurrentZone.hour.toString().padStart(2, '0')}:${dateTimeInCurrentZone.minute.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        ""
    }
}