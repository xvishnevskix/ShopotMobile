package org.videotrade.shopot.presentation.screens.contacts

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.ProfileComponents.CreateChatHeader
import org.videotrade.shopot.presentation.screens.main.MainScreen
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.edit_group_name
import kotlin.math.abs


class CreateGroupSecondScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContactsViewModel = koinInject()
        val selectedContacts = viewModel.selectedContacts
        val isSearching = remember { mutableStateOf(false) }
        val searchQuery = remember { mutableStateOf("") }
        
        val filteredContacts = if (searchQuery.value.isEmpty()) {
            selectedContacts
        } else {
            selectedContacts.filter {
                
                if (it.firstName !== null) {
                    it.firstName.contains(
                        searchQuery.value,
                        ignoreCase = true
                    ) || it.phone.contains(
                        searchQuery.value
                    )
                } else {
                    false
                }
            }
        }
        

            Box(
                modifier = Modifier
                    //background
                    .fillMaxWidth()
                    .background(Color(255, 255, 255))
            ) {
                SafeArea {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CreateChatHeader(
                        stringResource(MokoRes.strings.create_group),
                        isSearching = isSearching,
                        searchQuery = searchQuery,
                    )
                    LazyColumn(
                        modifier = Modifier

                            .fillMaxWidth()
                            .fillMaxHeight(0.8F)
                            .background(color = Color(255, 255, 255)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            CreateGroupInput()
                            ParticipantCountText(selectedContacts.size)
                        }
                        itemsIndexed(filteredContacts) { _, item ->
                            ChatItem(item = item)
                        }
                    }

                    Box(
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        CustomButton(
                            stringResource(MokoRes.strings.next),
                            {
                                navigator.push(
                                    MainScreen()
                                )

                            })
                    }
                }
                }
//                BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
            }
    }
}


@Composable
private fun ChatItem(item: ContactDTO) {
    
    
    Box(
        modifier = Modifier
            .padding(top = 22.dp)
            .clip(
                RoundedCornerShape(4.dp)
            )
            .background(Color(255, 255, 255))
            .fillMaxWidth()
            .clickable { }
    
    ) {
        Column(
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding().fillMaxWidth()
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding()
                ) {
                    Avatar(icon = item.icon, 80.dp)
                    Column(
                        modifier = Modifier.padding(start = 16.dp).fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = listOfNotNull(item.firstName, item.lastName)
                                .joinToString(" ")
                                .takeIf { it.isNotBlank() }
                                ?.let {
                                    if (it.length > 35) "${it.take(32)}..." else it
                                } ?: "",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            textAlign = TextAlign.Start,
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF000000)
                        )
                        Text(
                            text = "${item.phone}",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                            textAlign = TextAlign.Center,
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF979797),
                            modifier = Modifier.padding(top = 13.dp)
                        )
                        
                    }
                }
                
            }
            Divider(
                color = Color(0xFFD9D9D9).copy(alpha = 0.43f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 22.dp)
            )
        }
    }
}


@Composable
fun CreateGroupInput() {
    
    val message = remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .padding(start = 9.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.background(Color(0xFF2A293C), shape = CircleShape).size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.edit_group_name),
                contentDescription = "create group",
                modifier = Modifier.padding(start = 6.dp).size(22.dp)
            )
        }
        
        TextField(
            modifier = Modifier
                .width(232.dp)
                .padding(bottom = 15.dp, start = 25.dp)
                .background(Color(255, 255, 255)),
            
            label = { Text(stringResource(MokoRes.strings.enter_group_name)) },
            value = message.value,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                textAlign = TextAlign.Center,
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                color = Color(0xFF000000),
            ),
            onValueChange = { newText -> message.value = newText },
            colors = TextFieldDefaults.colors(
                disabledLabelColor = Color(0xff979797),
                focusedLabelColor = Color.Transparent,
                focusedContainerColor = Color(255, 255, 255),
                disabledContainerColor = Color(255, 255, 255),
                unfocusedContainerColor = Color(255, 255, 255),
                focusedIndicatorColor = Color(0xFFD9D9D9).copy(alpha = 0.9f),
                unfocusedIndicatorColor = Color(0xFFD9D9D9).copy(alpha = 0.43f),
                disabledIndicatorColor = Color(0xffc5c7c6)
            ),
            
            )
    }
}

@Composable
fun ParticipantCountText(count: Int) {
    Text(
        text = getParticipantCountText(count),
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
        color = Color(0xFF000000),
        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
    )
}

@Composable
fun getParticipantCountText(count: Int): String {
    val forms = arrayOf(
        stringResource(MokoRes.strings.participant),
        stringResource(MokoRes.strings.participants_1),
        stringResource(MokoRes.strings.participants_2)
    )
    return "$count ${getPluralForm(count, forms)}"
}

fun getPluralForm(number: Int, forms: Array<String>): String {
    val n = abs(number) % 100
    val n1 = n % 10
    return when {
        n in 11..19 -> forms[2]
        n1 == 1 -> forms[0]
        n1 in 2..4 -> forms[1]
        else -> forms[2]
    }
}