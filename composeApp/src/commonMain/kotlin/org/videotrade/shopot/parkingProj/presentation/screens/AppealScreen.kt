package org.videotrade.shopot.parkingProj.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.parkingProj.presentation.components.Common.BaseHeader
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomText
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomTextInputRow
import org.videotrade.shopot.parkingProj.presentation.components.Common.FontStyleType
import org.videotrade.shopot.parkingProj.presentation.components.Common.TextType

class AppealScreen : Screen {
    @Composable
    override fun Content() {
        var message by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader("Обращение")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))


                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color.White),
                    shadowElevation = 1.dp
                ) {
                    Box(
                        modifier = Modifier

                            .fillMaxSize()
                    ) {
                        BasicTextField(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            decorationBox = { innerTextField ->
                                if (message.isEmpty()) {
                                    CustomText(
                                        text = "Текст обращения",
                                        type = TextType.SECONDARY,
                                        fontStyle = FontStyleType.Regular,
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }
                Divider(
                    modifier = Modifier,
                    thickness = 1.dp,
                    color = Color(0xFFE2E2E2)
                )
                // Email
                    CustomTextInputRow(
                        label = "Ваш e-mail",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Введите адрес"
                    )
            }
        }
    }
}
