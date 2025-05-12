package org.videotrade.shopot.parkingProj.presentation.components.Map

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomButton
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomText
import org.videotrade.shopot.parkingProj.presentation.components.Common.FontStyleType
import org.videotrade.shopot.parkingProj.presentation.components.Common.TextType
import kotlin.math.ceil


//@Composable
//fun ParkingBottomSheet(
//    carNumber: String,
//    parkingNumber: String,
//    onTimeChanged: (LocalDateTime) -> Unit,
//    pricePerHour: String,
//    onConfirm: () -> Unit
//) {
//    val showTimePicker = remember { mutableStateOf(false) }
//
//    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
//
//    val startMinutes = now.minute + 30
//    val startHourOffset = startMinutes / 60
//    val initialHour = (now.hour + startHourOffset) % 24
//    val initialMinute = startMinutes % 60
//
//    val initialTime = LocalDateTime(
//        year = now.year,
//        monthNumber = now.monthNumber,
//        dayOfMonth = now.dayOfMonth,
//        hour = (now.hour + startHourOffset) % 24,
//        minute = startMinutes % 60,
//        second = 0,
//        nanosecond = 0
//    )
//
//    val selectedDate = remember { mutableStateOf(now.date) }
//    val selectedHour = remember { mutableStateOf(initialHour) }
//    val selectedMinute = remember { mutableStateOf(initialMinute) }
//
//    val selectedDateTime = LocalDateTime(
//        year = selectedDate.value.year,
//        monthNumber = selectedDate.value.monthNumber,
//        dayOfMonth = selectedDate.value.dayOfMonth,
//        hour = selectedHour.value,
//        minute = selectedMinute.value,
//        second = 0,
//        nanosecond = 0
//    )
//
//    val durationMinutes = calculateMinutesBetween(now, selectedDateTime)
//    val durationHours = durationMinutes / 60
//    val durationMins = durationMinutes % 60
//
//
//
//
//    Surface(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
//    ) {
//        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Column(horizontalAlignment = Alignment.End) {
//                    CustomText("Автомобиль", fontStyle = FontStyleType.Medium)
//                    Spacer(modifier = Modifier.height(16.dp))
//                    CustomText("Парковка", fontStyle = FontStyleType.Medium)
//                    Spacer(modifier = Modifier.height(24.dp))
//                    CustomText("Время", fontStyle = FontStyleType.Medium)
//                }
//
//                Spacer(modifier = Modifier.width(24.dp))
//
//                Column(horizontalAlignment = Alignment.Start) {
//                    CustomText(carNumber, type = TextType.BLUE)
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Column {
//                        CustomText(parkingNumber, type = TextType.BLUE)
//                        CustomText("$pricePerHour ₽ в час...", type = TextType.SECONDARY, fontSize = 12.sp)
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Column(modifier = Modifier.clickable {
//                        showTimePicker.value = true
//                    }) {
//                        CustomText(
//                            text = "${if (durationHours > 0) "$durationHours ч " else ""}$durationMins мин",
//                            type = TextType.BLUE
//                        )
//                        CustomText(
//                            text = "до ${formatFullDate(selectedDateTime)}",
//                            type = TextType.SECONDARY,
//                            fontSize = 12.sp
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            CustomButton(
//                text = "Припарковать за ${calculatePrice(
//                    calculateMinutesBetween(now, selectedDateTime)
//                )} ₽",
//                onClick = { onConfirm() }
//            )
//        }
//
//        if (showTimePicker.value) {
//            DateTimePickerDialog(
//                initialDate = selectedDate.value,
//                initialHour = selectedHour.value,
//                initialMinute = selectedMinute.value,
//                onDateSelected = { selectedDate.value = it },
//                onTimeSelected = { h, m ->
//                    selectedHour.value = h
//                    selectedMinute.value = m
//                },
//                onDismiss = { showTimePicker.value = false }
//            )
//        }
//    }
//}
//
//@Composable
//fun DateTimePickerDialog(
//    initialDate: kotlinx.datetime.LocalDate,
//    initialHour: Int,
//    initialMinute: Int,
//    onDateSelected: (kotlinx.datetime.LocalDate) -> Unit,
//    onTimeSelected: (hour: Int, minute: Int) -> Unit,
//    onDismiss: () -> Unit
//) {
//    val baseDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
//    val days = remember {
//        List(3) { i ->
//            val totalDays = baseDate.dayOfMonth + i
//            // Простейшая логика без учёта перехода месяца:
//            LocalDate(baseDate.year, baseDate.monthNumber, totalDays)
//        }
//    }
//
//    val hours = (0..23).toList()
//    val minutes = (0..59 step 5).toList()
//
//    var selectedDayIndex by remember { mutableStateOf(0) }
//    var selectedHour by remember { mutableStateOf(initialHour) }
//    var selectedMinute by remember { mutableStateOf(initialMinute) }
//
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        shape = RoundedCornerShape(16.dp),
//        color = Color.White
//    ) {
//        Column(Modifier.padding(16.dp)) {
//            Text("Выберите дату и время", fontSize = 16.sp)
//
//            Spacer(Modifier.height(12.dp))
//
//            Row(horizontalArrangement = Arrangement.SpaceBetween) {
//                LazyRow {
//                    items(days.size) { index ->
//                        val date = days[index]
//                        Box(
//                            modifier = Modifier
//                                .padding(4.dp)
//                                .background(
//                                    if (index == selectedDayIndex) Color.LightGray else Color.Transparent,
//                                    RoundedCornerShape(8.dp)
//                                )
//                                .clickable {
//                                    selectedDayIndex = index
//                                }
//                                .padding(horizontal = 12.dp, vertical = 8.dp)
//                        ) {
//                            Text("${date.dayOfMonth} ${getMonthName(date.monthNumber)}")
//                        }
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(8.dp))
//
//            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
//                LazyRow {
//                    items(hours.size) { index ->
//                        val hour = hours[index]
//                        Box(
//                            modifier = Modifier
//                                .padding(4.dp)
//                                .background(
//                                    if (hour == selectedHour) Color.LightGray else Color.Transparent,
//                                    RoundedCornerShape(8.dp)
//                                )
//                                .clickable {
//                                    selectedHour = hour
//                                }
//                                .padding(horizontal = 10.dp, vertical = 6.dp)
//                        ) {
//                            Text(hour.toString().padStart(2, '0'))
//                        }
//                    }
//                }
//
//                Spacer(Modifier.width(8.dp))
//
//                LazyRow {
//                    items(minutes.size) { index ->
//                        val min = minutes[index]
//                        Box(
//                            modifier = Modifier
//                                .padding(4.dp)
//                                .background(
//                                    if (min == selectedMinute) Color.LightGray else Color.Transparent,
//                                    RoundedCornerShape(8.dp)
//                                )
//                                .clickable {
//                                    selectedMinute = min
//                                }
//                                .padding(horizontal = 10.dp, vertical = 6.dp)
//                        ) {
//                            Text(min.toString().padStart(2, '0'))
//                        }
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(12.dp))
//
//            Column(Modifier.fillMaxWidth()) {
//                CustomButton("Отмена", onClick = {})
//                CustomButton("Готово", onClick =
//                {
//                    onDateSelected(days[selectedDayIndex])
//                    onTimeSelected(selectedHour, selectedMinute)
//                    onDismiss()
//                })
//            }
//        }
//    }
//}
//
//
//fun calculateMinutesBetween(start: LocalDateTime, end: LocalDateTime): Int {
//    val startTotalMinutes = start.dayOfMonth * 24 * 60 + start.hour * 60 + start.minute
//    val endTotalMinutes = end.dayOfMonth * 24 * 60 + end.hour * 60 + end.minute
//    return (endTotalMinutes - startTotalMinutes).coerceAtLeast(30)
//}
//
//fun formatTimeOnly(time: kotlinx.datetime.LocalTime): String {
//    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
//}
//
//fun getMonthName(month: Int): String = when (month) {
//    1 -> "янв"
//    2 -> "фев"
//    3 -> "мар"
//    4 -> "апр"
//    5 -> "май"
//    6 -> "июн"
//    7 -> "июл"
//    8 -> "авг"
//    9 -> "сен"
//    10 -> "окт"
//    11 -> "ноя"
//    12 -> "дек"
//    else -> ""
//}
//
//fun formatFullDate(dateTime: LocalDateTime): String {
//    val day = dateTime.dayOfMonth
//    val monthName = when (dateTime.monthNumber) {
//        1 -> "января"
//        2 -> "февраля"
//        3 -> "марта"
//        4 -> "апреля"
//        5 -> "мая"
//        6 -> "июня"
//        7 -> "июля"
//        8 -> "августа"
//        9 -> "сентября"
//        10 -> "октября"
//        11 -> "ноября"
//        12 -> "декабря"
//        else -> ""
//    }
//    val timeStr = formatTimeOnly(dateTime.time)
//    return "$day $monthName, $timeStr"
//}

@Composable
fun ParkingBottomSheet(
    carNumber: String,
    parkingNumber: String,
    onTimeChanged: (Int) -> Unit,
    pricePerHour: String,
    parkingMinutes: MutableState<Int>,
    onConfirm: () -> Unit
) {

    val showTimeScroller = remember { mutableStateOf(false) }
    val now = remember { Clock.System.now() }
    val timeZone = TimeZone.currentSystemDefault()

    val endTime = now
        .plus(parkingMinutes.value, DateTimeUnit.MINUTE)
        .toLocalDateTime(timeZone)

    val durationHours = parkingMinutes.value / 60
    val durationMins = parkingMinutes.value % 60

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .animateContentSize(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .background(color = Color(0xFFDBDBDB), shape = RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    CustomText("Автомобиль", fontStyle = FontStyleType.Medium)
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomText("Парковка", fontStyle = FontStyleType.Medium)
                    Spacer(modifier = Modifier.height(24.dp))
                    CustomText(
                        "Время",
                        fontStyle = FontStyleType.Medium,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .clickable { showTimeScroller.value = !showTimeScroller.value }
                    )

                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(horizontalAlignment = Alignment.Start) {
                    CustomText(carNumber, type = TextType.BLUE)
                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        CustomText(parkingNumber, type = TextType.BLUE)
                        CustomText("$pricePerHour ₽ в час...", type = TextType.SECONDARY, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        Modifier.clickable { showTimeScroller.value = !showTimeScroller.value }
                    ) {
                        CustomText(
                            text = "${if (durationHours > 0) "$durationHours ч " else ""}$durationMins мин",
                            type = TextType.BLUE
                        )
                        CustomText(
                            text = "до ${endTime.hour.toString().padStart(2, '0')}:${endTime.minute.toString().padStart(2, '0')}",
                            type = TextType.SECONDARY,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (showTimeScroller.value) {
                TimeScroller(
                    onDurationSelected = {
                        parkingMinutes.value = it
                        onTimeChanged(it)
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "Припарковать за ${calculatePrice(parkingMinutes.value)} ₽",
                onClick = { onConfirm() }
            )
        }
    }
}



private fun calculatePrice(minutes: Int): Int {
    val pricePerHour = 150
    return ceil(minutes / 60f * pricePerHour).toInt()
}


@Composable
fun TimeScroller(
    onDurationSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val now = remember { Clock.System.now() }
    val timeZone = TimeZone.currentSystemDefault()
    val itemWidth = 28.dp
    val listState = rememberLazyListState()
    val minDuration = 30
    val maxDuration = 96 * 60
    val stepMinutes = 1

    val durations = (minDuration..maxDuration step stepMinutes).toList()
    val initialIndex = durations.indexOf(minDuration)

//    // Прокрутка к 30 минутам
//    LaunchedEffect(Unit) {
//        listState.scrollToItem(initialIndex)
//        onDurationSelected(minDuration)
//    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {

        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .align(Alignment.Center)
                .width(2.dp)
                .height(24.dp)
                .background(Color.Red)
        )

//        // Слой с зелёной линией и кружком
//        LazyRow(
//            state = listState,
//            modifier = Modifier
//                .align(Alignment.Center)
//                .padding(bottom = 10.dp), // уровень полоски
//        ) {
//            items(durations.size) { index ->
//                val duration = durations[index]
//                Box(
//                    modifier = Modifier
//                        .width(itemWidth)
//                        .height(4.dp)
//                        .background(
//                            if (duration in minDuration..selectedDuration) Color.Green else Color.Transparent
//                        )
//                )
//            }
//        }
//
//        // Кружок в начале
//        LazyRow(
//            state = listState,
//            modifier = Modifier
//                .align(Alignment.Center)
//                .padding(bottom = 10.dp), // та же линия
//
//        ) {
//            items(durations.size) { index ->
//                val duration = durations[index]
//                Box(
//                    modifier = Modifier
//                        .width(itemWidth)
//                        .height(10.dp),
//                    contentAlignment = Alignment.CenterStart
//                ) {
//                    if (duration == minDuration) {
//                        Canvas(modifier = Modifier.size(10.dp)) {
//                            drawCircle(color = Color.Green)
//                        }
//                    }
//                }
//            }
//        }


        // Верхняя шкала времени (часы)
        Column(modifier = Modifier.fillMaxSize()) {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(durations.size) { index ->
                    val duration = durations[index]
                    val time = now.plus(duration, DateTimeUnit.MINUTE).toLocalDateTime(timeZone)
                    val isFullHour = time.minute == 0

                    Box(
                        modifier = Modifier
                            .width(itemWidth)
                            .height(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFullHour) {
                            CustomText(
                                text = "${time.hour.toString().padStart(2, '0')}:00",
                                fontSize = 10.sp,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Нижняя шкала делений
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(durations.size) { index ->
                    val duration = durations[index]
                    val time = now.plus(duration, DateTimeUnit.MINUTE).toLocalDateTime(timeZone)
                    val isMajor = time.minute == 0

                    Box(
                        modifier = Modifier
                            .width(itemWidth)
                            .height(40.dp)
                            .clickable { onDurationSelected(duration) },
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Горизонтальная линия
                            Canvas(modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .height(2.dp)) {
                                drawLine(
                                    color = Color(0xFF929292),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 4f
                                )
                            }

                            // Вертикальные деления
                            Canvas(modifier = Modifier
                                .align(Alignment.Center)
                                .height(if (isMajor) 16.dp else 8.dp)
                                .width(4.dp)) {
                                drawLine(
                                    color = if (isMajor) Color.Black else Color(0xFF929292),
                                    start = Offset(size.width, -size.height / 2),
                                    end = Offset(size.width, size.height / 2),
                                    strokeWidth = if (isMajor) 4f else 3f
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Слушаем scroll и обновляем выбранное значение
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val index = listState.firstVisibleItemIndex
        val duration = durations.getOrNull(index)?.coerceAtLeast(minDuration)
        if (duration != null) {
            onDurationSelected(duration)
        }
    }
}


