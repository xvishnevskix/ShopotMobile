package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.acceptCall
import shopot.composeapp.generated.resources.maksimus
import shopot.composeapp.generated.resources.microfon
import shopot.composeapp.generated.resources.randomUser
import shopot.composeapp.generated.resources.rejectCall
import shopot.composeapp.generated.resources.svgviewer_png_output


@Composable
fun rejectBtn(onClick: () -> Unit) {
    Column {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(80.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(255, 255, 255))
        ) {
            Image(
                painter = painterResource(Res.drawable.rejectCall),
                alignment = Alignment.Center,
                contentDescription = "Reject Call",
                modifier = Modifier.size(44.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 25.dp),
            text = "Отменить",
            fontSize = 16.sp,
            color = Color(255, 255, 255)
        )
    }
}

@Composable
fun aceptBtn(onClick: () -> Unit) {
    Column {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(80.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(41, 48, 60))
        ) {
            Image(
                painter = painterResource(Res.drawable.acceptCall),
                alignment = Alignment.Center,
                contentDescription = "Accept Call",
                modifier = Modifier.size(44.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 25.dp).align(Alignment.CenterHorizontally),
            text = "Принять",
            fontSize = 16.sp,
            color = Color(255, 255, 255)
        )
    }
}

@Composable
fun speakerBtn(onClick: () -> Unit) {
    Column {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(41, 48, 60))
        ) {
            Image(
                painter = painterResource(Res.drawable.svgviewer_png_output),
                alignment = Alignment.Center,
                contentDescription = "Speaker",
                modifier = Modifier.size(60.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 25.dp).align(Alignment.CenterHorizontally),
            text = "Динамик",
            fontSize = 14.sp,
            color = Color(255, 255, 255)
        )
    }
}

@Composable
fun microfonBtn(onClick: () -> Unit) {
    Column {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(41, 48, 60))
        ) {
            Image(
                painter = painterResource(Res.drawable.microfon),
                alignment = Alignment.Center,
                contentDescription = "Microfon",
                modifier = Modifier.size(60.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 25.dp).align(Alignment.CenterHorizontally),
            text = "Микрофон",
            fontSize = 14.sp,
            color = Color(255, 255, 255)
        )
    }
}
class CallScreen() : Screen {


    @Composable
    override fun Content() {


        var Photo: DrawableResource
        Photo = Res.drawable.maksimus

        Image(
            painter = painterResource(Photo),
            contentDescription = "image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(7.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                //.background(Color(0, 0, 0)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp)
                    .height(200.dp)
                    .background(
                        color = Color(255, 255, 255),
                        shape = RoundedCornerShape(100.dp)
                    )
                    .clip(CircleShape)
            ) {
                Image(
                    painter = painterResource(Photo),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(190.dp)
                        .height(190.dp)
                        .clip(RoundedCornerShape(100.dp))
                )

            }

            Text(
                modifier = Modifier
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Входящий звонок...",
                fontSize = 16.sp,
                color = Color(255, 255, 255)

            )
            var name: String
            name = "Максим Аркаев"
            Text(
                modifier = Modifier
                    .padding(top = 12.5.dp)
                    .align(Alignment.CenterHorizontally),
                text = "$name",
                fontSize = 24.sp,
                color = Color(255, 255, 255)

            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
                //.background(Color(0, 0, 0)),
            )



            Row(

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxSize()
            ) {
                rejectBtn { }
                aceptBtn {  }

            }
        }
    }
}


