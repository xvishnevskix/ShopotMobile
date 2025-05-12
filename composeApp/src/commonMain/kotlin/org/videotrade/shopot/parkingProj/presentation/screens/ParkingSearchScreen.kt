package videotrade.parkingProj.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.parkingProj.presentation.screens.MapScreen
import parkingproj.composeapp.generated.resources.Res
import parkingproj.composeapp.generated.resources.search
import shopot.composeapp.generated.resources.Res
import videotrade.parkingProj.presentation.components.Common.Common.BackIcon
import videotrade.parkingProj.presentation.components.Common.Common.CustomText
import videotrade.parkingProj.presentation.components.Common.Common.FontStyleType
import videotrade.parkingProj.presentation.components.Common.Common.TextType
import videotrade.parkingProj.presentation.components.Common.CustomTextField

class ParkingSearchScreen : Screen {

    @Composable
    override fun Content() {
        val searchQuery = remember { mutableStateOf("") }

        val parkingList = listOf(
            ParkingZone("Зона № 0304", "Москва", "380 ₽"),
            ParkingZone("Зона № 0316", "Москва", "380 ₽"),
            ParkingZone("Зона № 3104", "Москва", "150 ₽"),
            ParkingZone("Зона № 3116", "Москва", "150 ₽"),
            ParkingZone("Зона № 4016", "Москва", "40 ₽"),

        )
        val animatedFlags = remember { mutableStateListOf<Boolean>() }

        LaunchedEffect(parkingList.size) {
            // Инициализация списка флагов
            repeat(parkingList.size) {
                animatedFlags.add(false)
            }
        }

        val isVisible = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            isVisible.value = true
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F1F1))
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery.value = it }
            )
            Divider(
                color = Color.LightGray,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F1F1))
            ) {
                CustomText(
                    text = "Ближайшие парковки",
                    fontStyle = FontStyleType.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(parkingList) { index, parkingZone ->
                    val alreadyAnimated = animatedFlags.getOrNull(index) == true
                    var visible by remember(alreadyAnimated) { mutableStateOf(alreadyAnimated) }

                    LaunchedEffect(key1 = index) {
                        if (!alreadyAnimated) {
                            delay(index * 250L)
                            visible = true
                            animatedFlags[index] = true // Помечаем, что элемент уже анимирован
                        }
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                    ) {
                        ParkingZoneItem(parkingZone)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }


        }
    }
}

data class ParkingZone(val title: String, val city: String, val price: String)

@Composable
fun SearchBar(
    query: MutableState<String>,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(top = 24.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackIcon()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFF1F1F1)).padding(4.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.search),
                contentDescription = null,
                modifier = Modifier.padding(2.dp).size(16.dp),
                colorFilter = ColorFilter.tint(Color(0xFF007AFF))
            )

            Spacer(modifier = Modifier.width(4.dp))

            Box(Modifier.width(1.dp)
                .height(24.dp)
                .background(color = Color(0xFF007AFF), shape = RoundedCornerShape(size = 2.dp)))

            Spacer(modifier = Modifier.width(4.dp))

            CustomTextField(
                value = query.value,
                onValueChange = onQueryChange,
                placeholder = "Поиск парковок",
                modifier = Modifier.fillMaxWidth(0.75f),
                textAlign = TextAlign.Start,
                placeholderFontSize = 16.sp
            )
        }

        CustomText(
            text = "Отмена",
            type = TextType.BLUE,
            fontStyle = FontStyleType.Medium,
            modifier = Modifier.padding(start = 12.dp).fillMaxWidth().clickable { query.value = "" }
        )
    }
}

@Composable
fun ParkingZoneItem(
    parkingZone: ParkingZone
) {
    val navigator = LocalNavigator.currentOrThrow

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable { navigator.push(MapScreen())  }
                .padding(horizontal = 16.dp, vertical = 12.dp)

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CustomText(
                    text = parkingZone.price,
                    type = TextType.WHITE,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color(0xFF4D91D8), shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(Color(0xFF4D91D8))
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                CustomText(
                    text = parkingZone.title,
                    fontStyle = FontStyleType.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                CustomText(
                    text = parkingZone.city,
                    type = TextType.SECONDARY,
                    fontSize = 13.sp
                )
            }
        }
        Divider(
            color = Color.LightGray,
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )
    }
}