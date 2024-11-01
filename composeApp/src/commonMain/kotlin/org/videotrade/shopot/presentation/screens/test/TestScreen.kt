package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.rememberAsyncImagePainter
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel


class TestScreen : Screen {
    
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val commonViewModel: CommonViewModel = koinInject()
        val callViewModel: CallViewModel = koinInject()
        val imageBitmap = remember {
            mutableStateOf<String>("")
        }
        val navigator = LocalNavigator.currentOrThrow
        
        LaunchedEffect(Unit) {
        
        }
        val painter =
            rememberAsyncImagePainter("/storage/emulated/0/Android/data/org.videotrade.shopot.androidApp/files/Pictures/Images/655a1c7f-b565-45ba-90e9-b7fbaa5565a8655a1c7f-b565-45ba-90e9-b7fbaa5565a8")

//        "655a1c7f-b565-45ba-90e9-b7fbaa5565a8"
        MaterialTheme {
            SafeArea {
                Image(
                    painter = painter,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(400.dp),
                )
            }
        }
    }
    
    
}





