package org.videotrade.shopot.presentation.screens.apps

import org.videotrade.shopot.presentation.screens.contacts.ContactsViewModel


import Avatar
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.multiplatform.ContactsProviderFactory
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.components.Apps.AppsHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Common.validateFirstName
import org.videotrade.shopot.presentation.components.Contacts.ContactsSearch
import org.videotrade.shopot.presentation.components.Contacts.InviteContacts
import org.videotrade.shopot.presentation.components.Contacts.MakeGroup
import org.videotrade.shopot.presentation.components.ProfileComponents.CreateChatHeader
import org.videotrade.shopot.presentation.screens.profile.ProfileViewModel
import org.videotrade.shopot.presentation.tabs.ChatsTab
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.create_group
import shopot.composeapp.generated.resources.group
import shopot.composeapp.generated.resources.parking_app

class AppsScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val colors = MaterialTheme.colorScheme


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colors.surface)
        ) {
            SafeArea(padding = if (getPlatform() == Platform.Android) 0.dp else 16.dp)

            {
                Column(
                    Modifier.background(colors.background)
                ) {
                    AppsHeader(
                        text = stringResource(MokoRes.strings.applications),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        Modifier.padding(24.dp).clickable {

                        }
                    ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.parking_app),
                                    contentDescription = "Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(82.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Парковки\nКиргизии",
                                    style = TextStyle(
                                        color = colors.primary
                                        ,
                                        fontSize = 14.sp,
                                        lineHeight = 14.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        textAlign = TextAlign.Center
                                    ),
                                )
                            }
                    }
                }
            }
//                BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
        }

    }
}


