package org.videotrade.shopot.presentation.components.ProfileComponents

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Call.CallBar
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.search_icon

@Composable
fun CreateChatHeader(
    text: String,
    isSearching: MutableState<Boolean>,
) {
    val navigator = LocalNavigator.currentOrThrow
    

    
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
//            Crossfade(targetState = isSearching.value) { searching ->
//                if (searching) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        BasicTextField(
//                            value = searchQuery.value,
//                            onValueChange = { newText -> searchQuery.value = newText },
//                            singleLine = true,
//                            textStyle = textStyle,
//                            cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.Black),
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(35.dp)
//                                .background(androidx.compose.ui.graphics.Color.Transparent)
//                                .padding(start = 0.dp, end = 0.dp),
//                            decorationBox = { innerTextField ->
//                                Box(
//                                    modifier = Modifier
//                                        .background(androidx.compose.ui.graphics.Color.Transparent)
//                                        .padding(8.dp)
//                                ) {
//                                    if (searchQuery.value.isEmpty()) {
//                                        Text(
//                                            stringResource(MokoRes.strings.enter_name_or_phone),
//                                            style = textStyle.copy(color = androidx.compose.ui.graphics.Color.Gray)
//                                        )
//                                    }
//                                    innerTextField()
//                                }
//                            }
//                        )
//
//                        val rotationAngle by animateFloatAsState(
//                            targetValue = if (searching) 270f else 0f,
//                            animationSpec = tween(durationMillis = 10000, easing = LinearEasing)
//                        )
//
//                        Icon(
//                            imageVector = Icons.Default.Close,
//                            contentDescription = "Close",
//                            tint = Color(0xFF000000),
//                            modifier = Modifier
//                                .padding()
//                                .pointerInput(Unit) {
//                                    isSearching.value = false
//                                    searchQuery.value = ""
//                                }
//                                .rotate(rotationAngle)
//                        )
//                    }
//                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(35.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = text,
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            lineHeight = 24.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF373533),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),

                        )
                       Crossfade(targetState = isSearching.value) { searching ->
                           if (!searching) {
                               Image(
                                   painter = painterResource(Res.drawable.search_icon),
                                   contentDescription = "Search",
                                   modifier = Modifier
                                       .padding(end = 2.dp)
                                       .size(18.dp)
                                       .pointerInput(Unit) {
                                           isSearching.value = true
                                       },
                                   colorFilter = ColorFilter.tint(Color(0xff000000))
                               )
                           }
                       }
                    }
            }
        }
        Box(Modifier.padding(bottom = 18.dp)) {
            CallBar()
        }
}