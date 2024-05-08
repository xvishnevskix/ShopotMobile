package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatFooter(viewModel: ChatViewModel) {
    
    var text by remember { mutableStateOf("") }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
//        modifier = Modifier.fillMaxWidth().height(58.dp).clip(RoundedCornerShape(20.dp)).background(
//            Color(243, 244, 246)
//        ).padding(horizontal = 15.dp)
                
                modifier = Modifier.fillMaxWidth().background(
            Color(243, 244, 246)
        ).padding(horizontal = 15.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Back",
            modifier = Modifier.padding(end = 8.dp)
        )
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f), // Здесь можно добавить модификаторы, если это необходимо
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp), // Простой чёрный текст
            cursorBrush = SolidColor(Color.Black), // Чёрный цвет курсора
            visualTransformation = VisualTransformation.None, // Без визуальных преобразований
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty()) {
                        Text("Введите текст", color = Color.Gray, fontSize = 16.sp) // Подсказка
                    }
                    innerTextField() // Основное текстовое поле
                }
            }
        )
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Back",
            modifier = Modifier.padding(end = 8.dp).clickable {
                viewModel.addMessage(
                    MessageItem("2", text, true, "", "Мансур", "Дандаев", "", "")
                
                )
            }
        )
        
        
       
    }
       
    }
    
    
    

