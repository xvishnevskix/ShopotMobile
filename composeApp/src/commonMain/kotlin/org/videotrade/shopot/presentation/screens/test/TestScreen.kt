//package org.videotrade.shopot.presentation.screens.test
//
////import org.videotrade.shopot.multiplatform.encupsChachaMessage
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import cafe.adriel.voyager.core.screen.Screen
//import io.ktor.utils.io.charsets.Charsets
//import io.ktor.utils.io.core.toByteArray
//import org.videotrade.shopot.multiplatform.decupsChachaMessage
//import org.videotrade.shopot.multiplatform.encupsChachaMessage
//import org.videotrade.shopot.multiplatform.sharedSecret
//
//class TestScreen : Screen {
//    @Composable
//    override fun Content() {
//        var publicKey by remember { mutableStateOf("opsda") }
//
//        var errorMessage by remember { mutableStateOf("") }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Button(onClick = {
//                try {
//                    val publicKeyBytes = publicKey.toByteArray()
//
//                    val result = sharedSecret(publicKeyBytes)
//
//                    val result2 = encupsChachaMessage("privet", result[1])
//
//                    val result3 = decupsChachaMessage(
//                        cipher = result2.cipher,
//                        block = result2.block,
//                        authTag = result2.authTag,
//                        result[1]
//                    )
//
//                    println("result3 $result3")
//
//                } catch (e: Exception) {
//                    errorMessage = "Error: ${e.message}"
//                }
//            }) {
//                Text("Generate Shared Secret")
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//        }
//    }
//}
//
//


package org.videotrade.shopot.presentation.screens.test

//import org.videotrade.shopot.multiplatform.encupsChachaMessage
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import io.github.vinceglb.filekit.core.PickerType
import io.ktor.util.decodeBase64Bytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.vectorResource
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.theme.LocalThemeIsDark
import shopot.composeapp.generated.resources.IndieFlower_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.cyclone
import shopot.composeapp.generated.resources.ic_cyclone
import shopot.composeapp.generated.resources.ic_dark_mode
import shopot.composeapp.generated.resources.ic_light_mode
import shopot.composeapp.generated.resources.ic_rotate_right
import shopot.composeapp.generated.resources.stop
import shopot.composeapp.generated.resources.theme
import kotlin.random.Random

class TestScreen : Screen {
    @Composable
    override fun Content() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = org.jetbrains.compose.resources.stringResource(Res.string.cyclone),
                fontFamily = FontFamily(Font(Res.font.IndieFlower_Regular)),
                style = MaterialTheme.typography.displayLarge
            )
            
            var isAnimate by remember { mutableStateOf(false) }
            val transition = rememberInfiniteTransition()
            val rotate by transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing)
                )
            )
            
            Image(
                modifier = Modifier
                    .size(250.dp)
                    .padding(16.dp)
                    .run { if (isAnimate) rotate(rotate) else this },
                imageVector = vectorResource(Res.drawable.ic_cyclone),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                contentDescription = null
            )
            
            ElevatedButton(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .widthIn(min = 200.dp),
                onClick = { isAnimate = !isAnimate },
                content = {
                    Icon(vectorResource(Res.drawable.ic_rotate_right), contentDescription = null)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                }
            )
            
            var isDark by LocalThemeIsDark.current
            val icon = remember(isDark) {
                if (isDark) Res.drawable.ic_light_mode
                else Res.drawable.ic_dark_mode
            }
            
            ElevatedButton(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    .widthIn(min = 200.dp),
                onClick = { isDark = !isDark },
                content = {
                    Icon(vectorResource(icon), contentDescription = null)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(org.jetbrains.compose.resources.stringResource(Res.string.theme))
                }
            )
            
            Text("Moko resources:")
            Text(
                text = dev.icerock.moko.resources.compose.stringResource(MokoRes.strings.hello_world)
            )
            LanguageSelector()
            
            Text("Shopot")
            
        }
    }
    
    
    @Composable
    fun LanguageSelector() {
        Column {
            Button(onClick = { updateLocale("en") }) {
                Text(text = "English")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { updateLocale("ru") }) {
                Text(text = "Русский")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { updateLocale("es") }) {
                Text(text = "Español")
            }
        }
    }
    
    fun updateLocale(locale: String) {
        StringDesc.localeType = StringDesc.LocaleType.Custom(locale)
        // Вставьте логику для перерисовки UI, если необходимо
    }
}



