package org.videotrade.shopot.presentation.screens.test


import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import com.mmk.kmpnotifier.notification.NotifierManager
import kotlinx.coroutines.launch
import org.videotrade.shopot.multiplatform.BackgroundTaskManagerFactory

class TestScreen : Screen {
    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        
        
        Button({
            
            coroutineScope.launch {
                
                NotifierManager.getLocalNotifier().notify("Privet", "Andrey")
                
//                BackgroundTaskManagerFactory.create().scheduleTask()
            }
            
            
        }, content = {
            
            Text("ASDDSAD")
        })

//        ZoomableImage()
    }
}




