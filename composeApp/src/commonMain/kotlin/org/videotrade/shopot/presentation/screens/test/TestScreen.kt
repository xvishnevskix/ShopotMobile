package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.decupsMessage
import org.videotrade.shopot.api.encupsMessage
import org.videotrade.shopot.presentation.components.Common.SafeArea
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res


class TestScreen : Screen {
    
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var footerText by remember { mutableStateOf("") }
        var textRes by remember { mutableStateOf("") }
        
        SafeArea {
            
            Column {
                
                Button(onClick = {
                    val encupsRes = encupsMessage(footerText)
                    
                    println("encupsRes $encupsRes")
                    
                    val decupsRes = decupsMessage(Json.encodeToString(encupsRes))
                    
                    println("decupsRes $decupsRes")
                    if (decupsRes != null) {
                        textRes = decupsRes
                    }
                    
                }, content = { Text("AAAA") })
                
                
                BasicTextField(
                    value = footerText,
                    onValueChange = { newText ->
                        footerText =
                            newText
                    },
                    modifier = Modifier
                        .heightIn(max = 130.dp, min = 56.dp)
                        .padding(16.dp)
                        .fillMaxWidth(), // Для обеспечения выравнивания по ширине
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                        fontWeight = FontWeight(400),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        textAlign = TextAlign.Start
                    ),
                    cursorBrush = SolidColor(colors.primary),
                    visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences // Заставляет начинать с заглавной буквы
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(), // Обеспечивает выравнивание текста по центру
                            contentAlignment = Alignment.CenterStart // Центрируем внутреннее поле
                        ) {
                            if (footerText.isEmpty()) {
                                Text(
                                    stringResource(MokoRes.strings.write_message),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                    fontWeight = FontWeight(400),
                                    color = colors.secondary,
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    textAlign = TextAlign.Start
                                )
                            }
                            innerTextField() // Вставка текстового поля в Box
                        }
                    },
                )
                
                
                Text(textRes)
            }
            
            
        }
        
    }
}


