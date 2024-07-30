package org.videotrade.shopot.presentation.components.Auth

import androidx.compose.foundation.background
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
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.SharedRes
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular



@Composable
fun PhoneInput(
    textState: MutableState<TextFieldValue>
) {

    var countryCode by remember { mutableStateOf("+7") }
    val countries = listOf(
        "+7" to "\uD83C\uDDF7\uD83C\uDDFA   ${stringResource(SharedRes.strings.ru)}",
        "+995" to "\uD83C\uDDEC\uD83C\uDDEA   ${stringResource(SharedRes.strings.ge)}",
        "+374" to "\uD83C\uDDE6\uD83C\uDDF2   ${stringResource(SharedRes.strings.am)}",
        "+375" to "\uD83C\uDDE7\uD83C\uDDFE   ${stringResource(SharedRes.strings.by)}",
        "+996" to "\uD83C\uDDF0\uD83C\uDDEC   ${stringResource(SharedRes.strings.kg)}",
        "+992" to "\uD83C\uDDF9\uD83C\uDDEF   ${stringResource(SharedRes.strings.tj)}",
        "+998" to "\uD83C\uDDFA\uD83C\uDDFF   ${stringResource(SharedRes.strings.uz)}",
        "+371" to "\uD83C\uDDF1\uD83C\uDDFB   ${stringResource(SharedRes.strings.lv)}",
        "+63" to "\uD83C\uDDF5\uD83C\uDDED   ${stringResource(SharedRes.strings.ph)}"
    )

    val getPhoneNumberLength = { code: String ->
        when (code) {
            "+7", "+374", "+371" -> 12
            "+63", "+996", "+375", "+995", "+992", "+998" -> 13
            else -> 12
        }
    }

    val phoneNumberLength = getPhoneNumberLength(countryCode)

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        CountryPicker(
            selectedCountryCode = countryCode,
            countries = countries,
            onCountrySelected = { selectedCode ->
                countryCode = selectedCode
                textState.value = TextFieldValue(text = selectedCode, selection = TextRange(selectedCode.length))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = textState.value,
            onValueChange = { newTextValue ->
                val newText = newTextValue.text.filter { char -> char.isDigit() || char == '+' }
                val cursorPosition = newText.length
                if (newText.startsWith(countryCode) && newText.length <= phoneNumberLength) {
                    textState.value = TextFieldValue(text = newText, selection = TextRange(cursorPosition))
                } else if (!newText.startsWith(countryCode)) {
                    val adjustedText = countryCode + newText.drop(countryCode.length)
                        .take(phoneNumberLength - countryCode.length)
                    textState.value = TextFieldValue(text = adjustedText, selection = TextRange(adjustedText.length))
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                letterSpacing = TextUnit(1.7F, TextUnitType.Sp),
                lineHeight = 20.sp,
                color = Color(0xFF000000)
            ),
            modifier = Modifier
                .shadow(3.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth(0.9f).background(Color(0xFFFFFFFF))
                .padding(start = 15.dp, top = 19.dp, bottom = 15.dp)

        )
    }
}

@Composable
fun CountryPicker(
    selectedCountryCode: String,
    countries: List<Pair<String, String>>,
    onCountrySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCountry = countries.find { it.first == selectedCountryCode } ?: countries.first()

    Box(
        modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .clickable { expanded = !expanded }.fillMaxWidth(0.9f)
                    .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {


                Text(
                    "${selectedCountry.second}",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 24.sp,
                    color = Color(0xFF000000),
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { expanded = !expanded },
                    tint = Color.Black
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier

                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(5.dp),
                    offset = DpOffset(x = 0.dp, y = 210.dp),


                    ) {
                    countries.forEach { country ->
                        DropdownMenuItem(onClick = {
                            onCountrySelected(country.first)
                            expanded = false
                        },
                            text = {
                                Text(
                                    "${country.second} (${country.first})",
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                    lineHeight = 24.sp,
                                    color = Color(0xFF000000),
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                        )
                    }
                }
            }
            Divider(
                color = Color(0xFF979797),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth(0.9F)
            )
        }
    }
}


//@Composable
//fun PhoneInput(textState : MutableState<String>) {
//
//    Box(
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(10.dp))
//            .shadow(1.dp)
//            .background(Color.White)
//            .padding(horizontal = 16.dp, vertical = 12.dp)
//    ) {
//        BasicTextField(
//            value = textState.value,
//            onValueChange = { newValue ->
//                if (newValue.length <= 12) {
//                    textState.value = newValue.filter { it.isDigit() || it == '+' }
//                }
//            },
//            textStyle = TextStyle(
//                color = MaterialTheme.colorScheme.onSurface,
//                fontSize = MaterialTheme.typography.bodyLarge.fontSize
//            ),
//            modifier = Modifier.fillMaxWidth() // Заполнение максимальной ширины внутри Box
//        )
//    }
//}
