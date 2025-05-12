package videotrade.parkingProj.presentation.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import parkingproj.composeapp.generated.resources.Res
import parkingproj.composeapp.generated.resources.arrow_left
import parkingproj.composeapp.generated.resources.car
import parkingproj.composeapp.generated.resources.settings_dots
import videotrade.parkingProj.presentation.components.Common.Common.CustomText
import videotrade.parkingProj.presentation.components.Common.Common.FontStyleType
import videotrade.parkingProj.presentation.components.Common.Common.TextType
import videotrade.parkingProj.presentation.components.Common.Title


class CarsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf1f1f1))
        ) {
            BaseHeader("Автомобили")

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                ,
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomText(
                        text = "Припаркованные автомобили нельзя удалять\n" +
                                "и редактировать",
                        type = TextType.SECONDARY,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CarCard(isDefault = true)
                    Spacer(modifier = Modifier.height(16.dp))
                    CarCard(isDefault = false)
                    Spacer(modifier = Modifier.height(16.dp))
                    CarCard(isDefault = false)
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.background(Color.White)
                            .height(48.dp)
                            .fillMaxWidth()
                            .clickable {
                                navigator.push(NewCarScreen())
                            }
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CustomText(
                            text = "Добавить автомобиль",
                            type = TextType.BLUE,
                            fontStyle = FontStyleType.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CarCard(
    isDefault: Boolean = false,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)

    ) {
        if (isDefault) {
            Column {
                CustomText("Автомобиль по умолчанию",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    fontSize = 12.sp
                )
                Divider(
                    modifier = Modifier,
                    thickness = 1.dp,
                    color = Color(0xFFE2E2E2)
                )
            }
        }


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            Column {
                CustomText("Автомобиль", fontStyle = FontStyleType.Medium)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(Res.drawable.car),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.width(6.dp))
                    CustomText("а 929 вт 50", isUppercase = true, fontStyle = FontStyleType.Medium)
                }
            }

            Image(
                modifier = Modifier.size(width = 13.6.dp, height = 3.dp),
                painter = painterResource(Res.drawable.settings_dots),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}