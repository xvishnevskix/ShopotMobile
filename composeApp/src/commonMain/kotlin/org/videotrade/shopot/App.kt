package org.videotrade.shopot
//
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.unit.dp
//import cafe.adriel.voyager.navigator.Navigator
//import org.videotrade.shopot.presentation.screens.intro.IntroScreen
//import shopot.composeapp.generated.resources.*
//import org.videotrade.shopot.theme.AppTheme
//import org.videotrade.shopot.theme.LocalThemeIsDark
//import org.jetbrains.compose.resources.Font
//import org.jetbrains.compose.resources.stringResource
//import org.jetbrains.compose.resources.vectorResource
//import org.koin.core.context.KoinContext
//import cafe.adriel.voyager.transitions.SlideTransition
//import androidx.compose.material3.Surface
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.sp
//
//@Composable
//internal fun App() = AppTheme {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .windowInsetsPadding(WindowInsets.safeDrawing)
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
////        Text(
////            text = stringResource(Res.string.cyclone),
////            fontFamily = FontFamily(Font(Res.font.Montserrat)),
////            style = MaterialTheme.typography.displayLarge
////        )
//        Text(
//            text = "sssssss",
//            fontFamily = FontFamily(Font(Res.font.Montserrat)),
//            fontWeight = FontWeight.Normal,
//            fontSize = 30.sp,
//
//        )
//        var isAnimate by remember { mutableStateOf(false) }
//        val transition = rememberInfiniteTransition()
//        val rotate by transition.animateFloat(
//            initialValue = 0f,
//            targetValue = 360f,
//            animationSpec = infiniteRepeatable(
//                animation = tween(1000, easing = LinearEasing)
//            )
//        )
//
//        Image(
//            modifier = Modifier
//                .size(250.dp)
//                .padding(16.dp)
//                .run { if (isAnimate) rotate(rotate) else this },
//            imageVector = vectorResource(Res.drawable.ic_cyclone),
//            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
//            contentDescription = null
//        )
//
//        ElevatedButton(
//            modifier = Modifier
//                .padding(horizontal = 8.dp, vertical = 4.dp)
//                .widthIn(min = 200.dp),
//            onClick = { isAnimate = !isAnimate },
//            content = {
//                Icon(vectorResource(Res.drawable.ic_rotate_right), contentDescription = null)
//                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
//                Text(
//                    stringResource(if (isAnimate) Res.string.stop else Res.string.run)
//                )
//            }
//        )
//
//        var isDark by LocalThemeIsDark.current
//        val icon = remember(isDark) {
//            if (isDark) Res.drawable.ic_light_mode
//            else Res.drawable.ic_dark_mode
//        }
//
//        ElevatedButton(
//            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
//            onClick = { isDark = !isDark },
//            content = {
//                Icon(vectorResource(icon), contentDescription = null)
//                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
//                Text(stringResource(Res.string.theme))
//            }
//        )
//
//        TextButton(
//            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
//            onClick = { openUrl("https://github.com/terrakok") },
//        ) {
//            Text(stringResource(Res.string.open_github))
//        }
//    }
//
//}
//
//
//internal expect fun openUrl(url: String?)


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.KoinContext
import org.videotrade.shopot.presentation.screens.common.TestScreen
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    
    KoinContext {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Navigator(
//                ChatScreen(
//                ChatItem("2", true, "", "Мансур", "Дандаев", "", 2, "10", "","306e5bbb-e2db-4480-9f85-ca0a4b1b7a0b")
//                )
//                CallScreen(
//                    ChatItem(
//                        "2",
//                        true,
//                        "",
//                        "Мансур",
//                        "Дандаев",
//                        "",
//                        2,
//                        "10",
//                        null,
//                        "306e5bbb-e2db-4480-9f85-ca0a4b1b7a0b"
//                    )
//                )

//                MainScreen()
                        IntroScreen()
//                                SignUpScreen("+79388899885")
                //CallScreen()
                // IncomingCallScreen()
//                CreateChatScreen()
//                MainScreen()
//                TestScreen()
            ) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}

//
//package org.videotrade.shopot.presentation.screens.common
//
////import org.videotrade.shopot.multiplatform.MediaProviderFactory
////import org.videotrade.shopot.multiplatform.loadImage
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.dp
//import cafe.adriel.voyager.core.screen.Screen
//import com.preat.peekaboo.image.picker.SelectionMode
//import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
//import com.preat.peekaboo.image.picker.toImageBitmap
//import io.ktor.client.HttpClient
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.client.statement.HttpResponse
//import io.ktor.client.statement.bodyAsText
//import io.ktor.client.statement.request
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
//import io.ktor.http.isSuccess
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.buildJsonObject
//import kotlinx.serialization.json.jsonObject
//import kotlinx.serialization.json.jsonPrimitive
//import kotlinx.serialization.json.put
//import org.videotrade.shopot.api.EnvironmentConfig
//import org.videotrade.shopot.api.addValueInStorage
//import org.videotrade.shopot.api.getValueInStorage
//import org.videotrade.shopot.presentation.components.Common.SafeArea
//
//class TestScreen : Screen {
//    @Composable
//    override fun Content() {
//        MediaPickerSample()
//    }
//}
//
//@Composable
//fun MediaPickerSample() {
//    val scope = rememberCoroutineScope()
//    val byteArray = remember { mutableStateOf<ByteArray?>(null) }
//    var images by remember { mutableStateOf<ImageBitmap?>(null) }
//
//
//    val singleImagePicker = rememberImagePickerLauncher(
//        selectionMode = SelectionMode.Single,
//        scope = scope,
//        onResult = { byteArrays ->
//            byteArrays.firstOrNull()?.let {
//                // Process the selected images' ByteArrays.
//                println(it)
//                images = it.toImageBitmap()
//
//                byteArray.value = it
//            }
//        }
//    )
//
//
//    SafeArea {
//        Row {
//            images?.let {
//                Image(
//                    modifier = Modifier
//                        .size(220.dp),
//                    bitmap = it,
//
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop
//                )
//            }
//            Button(
//                onClick = {
//                    singleImagePicker.launch()
//                }
//            ) {
//                Text("Pick Single Image")
//            }
//        }
//
//    }
//
//}
