package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.presentation.components.Main.UserComponentItem
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Common.SafeArea
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.smart_encryption

@Composable
fun MainContentComponent(drawerState: DrawerState, chatState: List<UserItem>) {

    SafeArea {


        Column(

        ) {
            HeaderMain(drawerState)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Чаты",
                    modifier = Modifier.padding(bottom = 15.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp,
                    color = Color(0xFF000000)
                )


                LazyColumn {
                    items(chatState) { item ->
                        // Разметка для всех элементов списка
                        UserComponentItem(item)
                    }
                    item {
                        // Разметка для дополнительного элемента в конце списка
                        Column(
                            modifier = Modifier.padding(top = 40.dp, bottom = 10.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,

                            ) {
                                Image(
                                    modifier = Modifier.size(27.dp),
                                    painter = painterResource(Res.drawable.smart_encryption),
                                    contentDescription = null,
                                )
                            }
                            Row(

                            ) {
                                Text(
                                    "Все чаты зашифрованы  ",
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                    lineHeight = 20.sp,
                                    color = Color(0xFF000000),
                                )
                                Text(
                                    "ассиметричным и постквантовым шифрованием",
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                    lineHeight = 20.sp,
                                    color = Color(0xFF219653),
                                )
                            }
                        }
                    }
                }

            }

        }
    }

}