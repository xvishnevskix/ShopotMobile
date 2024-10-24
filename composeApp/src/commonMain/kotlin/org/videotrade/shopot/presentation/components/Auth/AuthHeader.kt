package org.videotrade.shopot.presentation.components.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ReloadRes
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.intro.WelcomeScreen
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.auth_logo

@Composable
fun AuthHeader(text: String, f: Float = 0.55F) {
    val navigator = LocalNavigator.currentOrThrow
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            containerColor = Color(0xFFF3F4F6),
            onDismissRequest = {
                showDialog.value = false
            },

            text = {
                Text(
                    "${stringResource(MokoRes.strings.are_you_sure_you_want_to_go_out)}\n${stringResource(MokoRes.strings.you_will_be_returned_to_the_initial_login_screen)}",
                    fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    color = Color(0xFF000000)
                )

            },

            dismissButton = {
                CustomButton(
                    stringResource(MokoRes.strings.no),
                    { scope ->
                        scope.launch {
                            showDialog.value = false

                        }
                    },
                    130.dp
                )
            },
            confirmButton = {
                CustomButton(
                    stringResource(MokoRes.strings.yes),
                    { scope ->
                        scope.launch {
                            showDialog.value = false
                            navigator.push(
                                WelcomeScreen()
                            )
                        }
                    },
                    130.dp
                )
            },
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 30.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
//        Icon(
//            imageVector = Icons.Default.ArrowBack,
//            contentDescription = "Back",
//            modifier = Modifier.padding(end = 8.dp).clickable {
//                showDialog = true
//            }.width(20.dp),
//            tint = Color.Black
//        )
        Box(modifier = Modifier.clickable {
            showDialog.value = true
        }.padding(start = 8.dp, end = 8.dp)) {
            Image(
                modifier = Modifier
                    .size(width = 7.dp, height = 14.dp),
                painter = painterResource(Res.drawable.arrow_left),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center,
                color = Color(0xFF373533)
            )
        )

        Spacer(modifier = Modifier.width(20.dp))
    }
}

