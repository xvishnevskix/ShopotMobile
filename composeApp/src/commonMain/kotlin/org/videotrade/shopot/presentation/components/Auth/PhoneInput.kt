package org.videotrade.shopot.presentation.components.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.arrow_left

val getPhoneNumberLength = { code: String ->
    when (code) {
        "+7", "+374", "+371" -> 12
        "+63", "+996", "+375", "+995", "+992", "+998" -> 13
        else -> 12
    }
}


@Composable
fun PhoneInput(
    textState: MutableState<TextFieldValue>,
    countryCode: String,
    onCountrySelected: () -> Unit
) {
    Row(
        modifier = Modifier.width(262.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CountryPicker(
            selectedCountryCode = countryCode,
            onCountrySelected = onCountrySelected
        )

        Spacer(modifier = Modifier.width(16.dp))

        BasicTextField(
            value = textState.value,
            onValueChange = { newTextValue ->
                // Оставляем только цифры в номере
                val newText = newTextValue.text.filter { char -> char.isDigit() }
                val cursorPosition = newText.length
                if (newText.length <= (getPhoneNumberLength(countryCode) - countryCode.length)) {
                    textState.value = TextFieldValue(text = newText, selection = TextRange(cursorPosition))
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                lineHeight = 16.sp,
                color = Color(0xFF373533),
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(width = 1.dp, color = Color(0x33373533), shape = RoundedCornerShape(16.dp))
                .background(Color(0xFFFFFFFF))
                .width(161.dp)
                .height(56.dp)
                .padding(start = 16.dp, top = 20.dp, bottom = 20.dp)
        )
    }
}

@Composable
fun CountryPicker(
    selectedCountryCode: String,
    onCountrySelected: () -> Unit


) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .height(56.dp)
            .width(93.dp)
            .background(Color(0xFFF7F7F7))
            .clickable {
                onCountrySelected()
            },
        contentAlignment = Alignment.Center
    ) {


            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
                ) {


                Text(
                    "${selectedCountryCode}",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    lineHeight = 16.sp,
                    color = Color(0xFF373533),
                )

//                Icon(
//                    imageVector = Icons.Default.ArrowDropDown,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .clickable { expanded = !expanded },
//                    tint = Color.Black
//                )

                Image(
                    modifier = Modifier
                        .rotate(270f)
                        .size(width = 5.dp, height = 12.dp),
                    painter = painterResource(Res.drawable.arrow_left),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

//                DropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false },
//                    modifier = Modifier
//
//                        .fillMaxWidth()
//                        .background(Color.White)
//                        .padding(5.dp),
//                    offset = DpOffset(x = 0.dp, y = 210.dp),
//
//
//                    ) {
//                    countries.forEach { country ->
//                        DropdownMenuItem(onClick = {
//                            onCountrySelected(country.first)
//                            expanded = false
//                        },
//                            text = {
//                                Text(
//                                    "${country.second} (${country.first})",
//                                    textAlign = TextAlign.Center,
//                                    fontSize = 18.sp,
//                                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                                    lineHeight = 24.sp,
//                                    color = Color(0xFF000000),
//                                    modifier = Modifier.padding(start = 5.dp)
//                                )
//                            }
//                        )
//                    }
//                }
            }


    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CountryPickerBottomSheet(
    countries: List<Pair<String, String>>,
    selectedCountryCode: String,
    onCountrySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(0.9f)
    ) {
        Text(
            text = "Выбор кода страны",
            style = TextStyle(
                fontSize = 18.sp,

                textAlign = TextAlign.Start
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        countries.forEach { country ->
            ListItem(
                modifier = Modifier.clickable {
                    onCountrySelected(country.first)
                },
                text = {
                    Text(
                        "${country.second} (${country.first})",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            )
            Divider()
        }
    }
}