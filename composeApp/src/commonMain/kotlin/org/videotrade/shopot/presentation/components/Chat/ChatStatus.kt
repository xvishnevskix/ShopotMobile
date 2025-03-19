//package org.videotrade.shopot.presentation.components.Chat
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.material.MaterialTheme.colors
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.TextUnit
//import androidx.compose.ui.unit.TextUnitType
//import androidx.compose.ui.unit.sp
//import org.jetbrains.compose.resources.Font
//import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
//import shopot.composeapp.generated.resources.ArsonPro_Medium
//import shopot.composeapp.generated.resources.Res
//
//@Composable
//fun ChatStatus(viewModel: ChatViewModel) {
//    val userStatuses by viewModel.userStatuses.collectAsState()
//    println("User statuses: $userStatuses") // ✅ Отладочный вывод
//
//    if (userStatuses.isEmpty()) {
//        println("No user statuses received!")
//        Text(
//            "Не в сети",
//            textAlign = TextAlign.Center,
//            fontSize = 16.sp,
//            lineHeight = 16.sp,
//            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
//            fontWeight = FontWeight(500),
//            color = MaterialTheme.colorScheme.secondary,
//            letterSpacing = TextUnit(0F, TextUnitType.Sp),
//        )
//    } else {
//        userStatuses.forEach { (userId, status) ->
//            val statusText = getStatusText(status)
//            println("User statuses: $userId -> $statusText") // ✅ Логируем статус
//
//            Text(
//                text = statusText,
//                textAlign = TextAlign.Center,
//                fontSize = 16.sp,
//                lineHeight = 16.sp,
//                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
//                fontWeight = FontWeight(500),
//                color = Color(0xFFCAB7A3),
//                letterSpacing = TextUnit(0F, TextUnitType.Sp),
//            )
//        }
//    }
//}
//
//// Функция для перевода статусов в понятный текст
//fun getStatusText(status: String): String {
//    return when (status) {
//        "ONLINE" -> "В сети"
//        "TYPING" -> "Печатает..."
//        "SENDING_FILE" -> "Отправляет файл"
//        "CHOOSING_STICKER" -> "Выбирает стикер"
//        "RECORDING_VOICE" -> "Записывает голосовое сообщение"
//        else -> "Не в сети"
//    }
//}