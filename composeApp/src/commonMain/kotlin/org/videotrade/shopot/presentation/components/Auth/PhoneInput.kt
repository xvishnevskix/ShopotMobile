package org.videotrade.shopot.presentation.components.Auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
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
import org.videotrade.shopot.presentation.components.Common.BackIcon
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_Regular
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
    hasError: Boolean,
    onCountrySelected: () -> Unit,
    animationTrigger: Boolean,
    showPhoneMenu: MutableState<Boolean>
) {

    // Анимация смещения для "тряски"
    val offsetX = remember { Animatable(0f) }

    // Цвет бордера: красный, если ошибка, иначе серый
    val borderColor by animateColorAsState(
        targetValue = if (hasError) Color(0xFFFF3B30) else Color(0x33373533),
        animationSpec = tween(durationMillis = 300)
    )

    // Запускаем анимацию тряски, если есть ошибка
    LaunchedEffect(animationTrigger) {
        if (hasError) {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 500
                    // Двигаем текстовое поле влево и вправо
                    -5f at 0
                    5f at 100
                    -5f at 200
                    5f at 300
                    0f at 400
                }
            )
        }
    }

    Row(
        modifier = Modifier.width(262.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CountryPicker(
            selectedCountryCode = countryCode,
            onCountrySelected = onCountrySelected,
            showPhoneMenu
        )

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .offset(x = offsetX.value.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                .background(Color(0xFFFFFFFF))
                .width(161.dp)
                .height(56.dp)
                .padding(start = 20.dp, top = 20.dp, bottom = 20.dp, end = 20.dp)
        ) {
            if (textState.value.text.isEmpty()) {
                Text(
                    text = "XXX XXX XX XX",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                        color = Color(0x80373533)
                    ),
                    modifier = Modifier
                )
            }

            BasicTextField(
                value = textState.value,
                onValueChange = { newTextValue ->
                    // Оставляем только цифры в номере
                    val newText = newTextValue.text.filter { char -> char.isDigit() }
                    val cursorPosition = newText.length
                    if (newText.length <= (getPhoneNumberLength(countryCode) - countryCode.length)) {
                        textState.value =
                            TextFieldValue(text = newText, selection = TextRange(cursorPosition))
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
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun CountryPicker(
    selectedCountryCode: String,
    onCountrySelected: () -> Unit,
    showPhoneMenu: MutableState<Boolean>

) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .height(56.dp)
            .width(93.dp)
            .background(Color(0xFFF7F7F7))
            .clickable {
                onCountrySelected()
                showPhoneMenu.value = !showPhoneMenu.value
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
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
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


@Composable
fun CountryPickerBottomSheet(
    showPhoneMenu: MutableState<Boolean>,
    countries: List<Pair<String, String>>,
    selectedCountryCode: String,
    onCountrySelected: (String) -> Unit,
    onBackClick: () -> Unit,

) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(1f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            Box(modifier = Modifier.clickable {
                onBackClick()
            }.padding(start = 8.dp, end = 8.dp)) {
                BackIcon()
            }

            Text(
                text = stringResource(
                    MokoRes.strings.select_country_code
                ),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    color = Color(0xFF373533)
                )
            )

            Spacer(modifier = Modifier.width(20.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column {
            Text(
                text = stringResource(
                    MokoRes.strings.selected
                ),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    color = Color(0xFF373533)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            val selectedCountry = countries.find { it.first == selectedCountryCode }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(size = 16.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 20.dp)
                ) {
                    selectedCountry?.let {
                        Text(
                            text = "${it.second}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                textAlign = TextAlign.Start,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                color = Color(0xFF373533)
                            )
                        )
                        Text(
                            text = "${it.first}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                textAlign = TextAlign.Start,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                color = Color(0xFF373533)
                            )
                        )
                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(40.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(
                    MokoRes.strings.list_of_other_countries
                ),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    color = Color(0xFF373533)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                item {
                    countries.forEach { country ->
                        val (flag, countryName) = country.second.split("   ", limit = 2)
                        val isSelected = country.first == selectedCountryCode
                        val textColor = if (isSelected) Color(0xFF373533) else Color(0x80373533)
                        val borderColor =
                            if (isSelected) Color(0xFF373533) else Color(0x33373533) // rgba(55, 53, 51, 0.2)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(color = Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(size = 16.dp)
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .clickable {
                                        onCountrySelected(country.first)
                                    }
                                    .fillMaxWidth()
                                    .padding(
                                        start = 16.dp,
                                        top = 20.dp,
                                        end = 16.dp,
                                        bottom = 20.dp
                                    )
                            ) {
                                // Смайлик и название страны
                                Row {
                                    Text(
                                        text = "${flag}",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                            fontWeight = FontWeight(400),
                                            textAlign = TextAlign.Start,
                                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                            color = Color(0xFF373533)
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${countryName}",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                            fontWeight = FontWeight(400),
                                            textAlign = TextAlign.Start,
                                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                            color = textColor
                                        )
                                    )
                                }
                                // Код страны без скобочек
                                Text(
                                    text = "${country.first}",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        textAlign = TextAlign.End,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        color = textColor
                                    )
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}