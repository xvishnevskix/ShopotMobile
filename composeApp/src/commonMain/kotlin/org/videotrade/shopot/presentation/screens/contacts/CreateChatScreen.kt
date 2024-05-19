package org.videotrade.shopot.presentation.screens.contacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.ProfileComponents.CreateChatHeader
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.create_group
import shopot.composeapp.generated.resources.person
import shopot.composeapp.generated.resources.search_main


class CreateChatScreen() : Screen {
    @Composable
    override fun Content() {

        val viewModel: ContactsViewModel = koinInject()
        val contacts = viewModel.contacts.collectAsState(initial = listOf()).value


        viewModel.fetchContacts()


        SafeArea {
            Box(
                modifier = Modifier
                    //background
                    .fillMaxSize()
                    .background(Color(255, 255, 255))
            ) {
                Column {
                    CreateChatHeader("Создать чат")
                    LazyColumn(
                        modifier = Modifier

                            .fillMaxSize()
                            .background(color = Color(255, 255, 255))
                    ) {
                        item {
                            makeA_group()
                        }
                        itemsIndexed(
                            contacts

                        ) { _, item ->
                            UserItem(item = item)

                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun makeA_group() {
    Box(
        modifier = Modifier
            .background(Color(255, 255, 255))
            .fillMaxSize()
            .padding(top = 10.dp, bottom = 42.dp)
            .clickable { }

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Image(
                    painter = painterResource(Res.drawable.create_group),
                    contentDescription = "back icon",
                    modifier = Modifier.size(56.dp)
                )

                Text(
                    text = "Создать группу",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                    textAlign = TextAlign.Center,
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .padding(start = 20.dp)

                )
            }
            Image(
                painter = painterResource(Res.drawable.arrowleft),
                contentDescription = "create group arrow",
                modifier = Modifier.size(18.dp)
            )

        }
    }
}


