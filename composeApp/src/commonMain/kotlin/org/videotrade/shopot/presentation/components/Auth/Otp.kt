package org.videotrade.shopot.presentation.components.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Otp(otpFields: SnapshotStateList<String>) {

    Box(
        modifier = Modifier
            .padding(top = 45.dp, bottom = 45.dp)
            .shadow(1.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        val focusRequesters = List(4) { FocusRequester() }

        Row(Modifier.padding(10.dp)) {
            otpFields.forEachIndexed { index, _ ->
                OutlinedTextField(
                    value = otpFields[index],
                    onValueChange = { input ->
                        // Фильтруем ввод так, чтобы оставались только цифры от 0 до 9
                        val filteredInput = input.filter { it.isDigit() }
                        if (filteredInput.length <= 1) {
                            otpFields[index] = filteredInput

                            if (filteredInput.isNotEmpty() && index < focusRequesters.size - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 18.sp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.None // Отключаем кнопки "Далее" и т.п.
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequesters[index])
                        .size(50.dp)
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    visualTransformation = VisualTransformation.None // Отключаем визуальную трансформацию
                )

                if (index < focusRequesters.size - 1) Spacer(modifier = Modifier.width(8.dp))
            }
        }

        DisposableEffect(Unit) {
            focusRequesters[0].requestFocus()
            onDispose { }
        }
    }
}