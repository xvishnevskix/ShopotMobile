package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.videotrade.shopot.presentation.components.Main.UserComponentItem
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Common.SafeArea

@Composable
fun MainContentComponent(drawerState: DrawerState, chatState:  List<UserItem>) {

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
                    modifier = Modifier.padding(bottom = 20.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold

                )

                LazyColumn {
                    itemsIndexed(chatState) { index, item ->
                        UserComponentItem(item)

                    }
                }

            }

        }
    }

}