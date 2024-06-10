package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular

@Composable
fun ChatFooter(chat: ChatItem, viewModel: ChatViewModel) {
    val scope = rememberCoroutineScope()
    
    var text by remember { mutableStateOf("") }
    
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                
                
                scope.launch {
                    viewModel.sendAttachments(
                        content = text,
                        fromUser = viewModel.profile.value.id,
                        chatId = chat.id,
                        it
                    )
                    
                    
                }
                
                
            }
        }
    )
    Box(
        modifier = Modifier
            .imePadding().padding(vertical = 15.dp)
    ) {
        
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().height(58.dp).clip(RoundedCornerShape(20.dp))
                .background(
                    Color(0xFFF3F4F6)
                ).padding(horizontal = 15.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(37.dp)
                    .background(color = Color(0xFF2A293C), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp).clickable {
                        singleImagePicker.launch()
                        
                    }
                )
            }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f), // Здесь можно добавить модификаторы, если это необходимо
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ), // Простой чёрный текст
                cursorBrush = SolidColor(Color.Black), // Чёрный цвет курсора
                visualTransformation = VisualTransformation.None, // Без визуальных преобразований
                decorationBox = { innerTextField ->
                    Box {
                        if (text.isEmpty()) {
                            Text(
                                "Написать...",
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 20.sp,
                                color = Color(0xFF979797),
                            )
                            // Подсказка
                        }
                        innerTextField() // Основное текстовое поле
                    }
                }
            )
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                modifier = Modifier.padding(end = 8.dp).clickable {
                    viewModel.sendMessage(
                        content = text,
                        fromUser = viewModel.profile.value.id,
                        chatId = chat.id,
                        notificationToken = chat.notificationToken,
                        attachments = emptyList()
                    )
                    
                    text = ""
                }
            )
            
            
        }
        
    }
}
    
    

