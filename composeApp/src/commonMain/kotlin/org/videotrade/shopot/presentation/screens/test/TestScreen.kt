package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.preat.peekaboo.image.picker.toImageBitmap
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.VideoPlayer
import org.videotrade.shopot.multiplatform.getAndSaveFirstFrame
import org.videotrade.shopot.multiplatform.getBuildVersion
import org.videotrade.shopot.multiplatform.simulateIncomingCall
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res


class TestScreen : Screen {

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val commonViewModel: CommonViewModel = koinInject()
        val callViewModel: CallViewModel = koinInject()



        LaunchedEffect(Unit) {
            val profileId = getValueInStorage("profileId")

            println("profileId $profileId")
            if (profileId != null) {
                callViewModel.connectionBackgroundWs(profileId)
            }

            callViewModel.initWebrtc()
        }


        MaterialTheme {
            SafeArea {
                Button(onClick = {
                    scope.launch {
                        callViewModel.makeCallBackground(
                            "f6EXjONXSduE_hP6plWaSl:APA91bHLw7h2Igk8uqu7Sc_V7hgPeWzlxa3GUGpafkXWQFB47lY5ZnhKwxWytYc98i39j5D_GjeYtbNLujGbemrWjiXYh5QLs9gFDEMdn9BZN7JSq9cKRuTm9lQj2ONUSXWNtzbUGMll",
                            "eb2f7045-592b-4304-bc8a-d14234777550"
                        )

//                        commonViewModel.sendNotify("Privet","","fPlJ64fZSxKSYZ4KgH5xdq:APA91bFtPyi8uPj5F0P3Bn6rwLuIhKCKKfz1JhgIA1AysC7x4irg2cTQ996xu15sArQDyN0XAeFhyN-KZm7pBCmyOpEgaSE714BLfMxSnytGR9Gcqtprx1nOEDs69IP6ifevGYn0ONXK" )


                    }
                }, content = {
                    Text("SendNotific")
                })
            }
        }
    }


}





