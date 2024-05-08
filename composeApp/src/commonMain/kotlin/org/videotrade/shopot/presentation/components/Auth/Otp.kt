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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Otp(otpFields: SnapshotStateList<String>) {

    Box(
        modifier = Modifier
            .padding(top =45.dp, bottom = 45.dp) // Общий внешний отступ
            .shadow(1.dp, RoundedCornerShape(10.dp)) // Тень с округлыми углами
            .clip(RoundedCornerShape(10.dp)) // Округление углов элемента
            .background(Color.White) // Фоновый цвет инпута
        ,
        contentAlignment = Alignment.Center


    ) {



        val focusRequesters = List(4) { FocusRequester() }

        Row (Modifier.padding(10.dp)) {
            otpFields.forEachIndexed { index, _ ->
                OutlinedTextField(
                    value = otpFields[index],
                    placeholder = {
                        Text(
                            "—", // Стиль плейсхолдера соответствующий вашему дизайну
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    },
                    onValueChange = {
                        if (it.length <= 1) {
                            otpFields[index] = it

                            if (it.isNotEmpty() && index < focusRequesters.size - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 18.sp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequesters[index])
                        .size(50.dp)
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
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