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


import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.jetbrains.compose.resources.Font
import org.koin.compose.KoinContext
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.theme.AppTheme
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular

@Composable
internal fun App() = AppTheme {
    
    KoinContext {
        
        Navigator(
            IntroScreen()
        ) { navigator ->
            SlideTransition(navigator)
        }
        
    }
}


