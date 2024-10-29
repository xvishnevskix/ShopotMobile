package org.videotrade.shopot.presentation.screens.test

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.MusicType
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel


class TestScreen : Screen {

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val commonViewModel: CommonViewModel = koinInject()
        val callViewModel: CallViewModel = koinInject()
        
        val navigator = LocalNavigator.currentOrThrow
        val musicPlayer = remember { AudioFactory.createMusicPlayer()  }
        

        MaterialTheme {
            SafeArea {
                Button(onClick = {
                    scope.launch {
                   try {
                       
                       musicPlayer.play("callee", true, MusicType.Ringtone)
                     
                   }catch (e:Exception){
                   
                   }
                    }
                }, content = {
                    Text("SendCall")
                })
            }
        }
    }


}





