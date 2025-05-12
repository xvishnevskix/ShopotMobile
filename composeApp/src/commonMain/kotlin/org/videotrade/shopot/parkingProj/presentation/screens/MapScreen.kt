package org.videotrade.shopot.parkingProj.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import shopot.composeapp.generated.resources.Res
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomButton
import org.videotrade.shopot.parkingProj.presentation.components.Map.Buttons.BalanceChip
import org.videotrade.shopot.parkingProj.presentation.components.Map.Buttons.CircularImageButton
import org.videotrade.shopot.parkingProj.presentation.components.Map.ParkingBottomSheet
import org.videotrade.shopot.parkingProj.presentation.components.Map.Buttons.ZoomControlButtons
import shopot.composeapp.generated.resources.location
import shopot.composeapp.generated.resources.menu
import shopot.composeapp.generated.resources.search
import videotrade.parkingProj.presentation.components.Map.MapUi

class MapScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val showSheet = remember { mutableStateOf(false) }
        val parkingMinutes = remember { mutableStateOf(30) }

        Box(modifier = Modifier.fillMaxSize()) {

            // Здесь будет карта
            MapUi()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // меню
                CircularImageButton(
                    image = Res.drawable.menu,
                    onClick = { navigator.push(MainSettingsScreen()) },
                )

                // Баланс
                BalanceChip(
                    balance = "685 ₽",
                    onTopUp = { navigator.push(TopUpScreen()) },
                )

                // Поиск
                CircularImageButton(
                    image = Res.drawable.search,
                    onClick = { navigator.push(ParkingSearchScreen())},
                )
            }

            // Вертикальный стек кнопок справа
            Column(
                modifier = Modifier
                    .padding(top = 200.dp)
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                ZoomControlButtons(
                    onZoomIn = { },
                    onZoomOut = { }
                )
                Spacer(modifier = Modifier.height(8.dp))
                CircularImageButton(
                    image = Res.drawable.location,
                    onClick = {
                        showSheet.value = !showSheet.value
                    },
                )
            }

            // Добавить авто
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .padding(horizontal = 16.dp)
            ) {
                CustomButton(
                    onClick = { navigator.push(NewCarScreen()) },
                    text = "Добавить автомобиль",
                )
            }
            if (showSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet.value = false },
                    sheetState = sheetState,
                    dragHandle = null,
                    containerColor = Color.White,
                ) {
                    ParkingBottomSheet(
                        carNumber = "А 929 ВТ 50",
                        parkingNumber = "3116",
                        onTimeChanged = { /* обновление времени */ },
                        pricePerHour = "150",
                        parkingMinutes,
                        onConfirm = {
                            showSheet.value = false
                            // логика парковки
                        }
                    )
                }
            }
        }
    }
}



