package org.videotrade.shopot.presentation.components.ProfileComponents

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
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
import co.touchlab.stately.freeze
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.presentation.screens.contacts.CreateChatScreen
import shopot.composeapp.generated.resources.Montserrat_Bold
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFProText_Regular
import shopot.composeapp.generated.resources.check_mark
import shopot.composeapp.generated.resources.dot_menu
import shopot.composeapp.generated.resources.search_icon

@Composable
fun CreateChatHeader(
    text: String,
    isSearching: MutableState<Boolean>,
    searchQuery: MutableState<String>,
) {
    val navigator = LocalNavigator.currentOrThrow

    val textStyle = TextStyle(
        color = androidx.compose.ui.graphics.Color.Black,
        fontSize = 14.sp,
        fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
        lineHeight = 20.sp,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 23.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Crossfade(targetState = isSearching.value) { searching ->
            if (searching) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicTextField(
                        value = searchQuery.value,
                        onValueChange = { newText -> searchQuery.value = newText },
                        singleLine = true,
                        textStyle = textStyle,
                        cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .height(35.dp)
                            .background(androidx.compose.ui.graphics.Color.Transparent)
                            .padding(start = 0.dp, end = 0.dp),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .background(androidx.compose.ui.graphics.Color.Transparent)
                                    .padding(8.dp)
                            ) {
                                if (searchQuery.value.isEmpty()) {
                                    Text("Введите имя или телефон", style = textStyle.copy(color = androidx.compose.ui.graphics.Color.Gray))
                                }
                                innerTextField()
                            }
                        }
                    )

                    val rotationAngle by animateFloatAsState(
                        targetValue = if (searching) 270f else 0f,
                        animationSpec = tween(durationMillis = 10000, easing = LinearEasing)
                    )

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .padding()
                            .clickable {
                                isSearching.value = false
                                searchQuery.value = ""
                            }
                            .rotate(rotationAngle)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().height(35.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(0.dp))

                    Text(
                        text = text,
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Image(
                        painter = painterResource(Res.drawable.search_icon),
                        contentDescription = "Search",
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(15.dp)
                            .clickable {
                                isSearching.value = true
                            }
                    )
                }
            }
        }
    }
}