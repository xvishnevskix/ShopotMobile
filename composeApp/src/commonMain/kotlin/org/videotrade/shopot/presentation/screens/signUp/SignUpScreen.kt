package org.videotrade.shopot.presentation.screens.signUp

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.InternalAPI
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ReloadRes
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.pencil_in_circle

data class SignUpTextState(
    var firstName: String = "",
    var lastName: String = "",
    var nickname: String = ""
)

class SignUpScreen(private val phone: String) : Screen {
    
    @OptIn(InternalAPI::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: IntroViewModel = koinInject()
        
        val scope = rememberCoroutineScope()
        val textState = remember { mutableStateOf(SignUpTextState()) }
        val byteArray = remember { mutableStateOf<ByteArray?>(null) }
        var images by remember { mutableStateOf<ImageBitmap?>(null) }
        val сommonViewModel: CommonViewModel = koinInject()
        
        val singleImagePicker = rememberImagePickerLauncher(
            selectionMode = SelectionMode.Single,
            scope = scope,
            onResult = { byteArrays ->
                byteArrays.firstOrNull()?.let {
                    
                    images = it.toImageBitmap()
                    
                    byteArray.value = it
                }
            }
        )
        
        
        SafeArea {
            AuthHeader("Создать аккаунт", 0.75F)
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier.padding(top = 70.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    item {
                        Box(modifier = Modifier.clickable {
                            singleImagePicker.launch()
                            
                        }) {
                            
                            
                            if (images !== null) {
                                Avatar(bitmap = images, size = 140.dp)
                                
                                
                            } else {
                                Avatar(icon = null, 140.dp)
                                Image(
                                    painter = painterResource(Res.drawable.pencil_in_circle),
                                    contentDescription = "Редактировать",
                                    modifier = Modifier.size(28.dp).align(Alignment.BottomEnd)
                                )
                            }
                            
                            
                        }
                    }
                    
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 35.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextFieldWithTitle(
                                title = "Имя",
                                value = textState.value.firstName,
                                onValueChange = {
                                    textState.value = textState.value.copy(firstName = it)
                                },
                                placeholder = "Имя"
                            )
                            
                            TextFieldWithTitle(
                                title = "Фамилия",
                                value = textState.value.lastName,
                                onValueChange = {
                                    textState.value = textState.value.copy(lastName = it)
                                },
                                placeholder = "Фамилия"
                            )
                            
                            TextFieldWithTitle(
                                title = "Придумайте ник",
                                value = textState.value.nickname,
                                onValueChange = {
                                    textState.value = textState.value.copy(nickname = it)
                                },
                                placeholder = "Придумайте ник"
                            )
                        }
                    }
                    
                    item {
                        Box(
                            modifier = Modifier.padding(top = 20.dp)
                        ) {
                            CustomButton(
                                "Создать аккаунт",
                                { scope ->
                                    scope.launch {
                                        val client = HttpClient(getHttpClientEngine())
                                        
                                        try {
                                            
                                            val icon = byteArray.value?.let {
                                                origin().sendFile(
                                                    "file/upload",
                                                    null,
                                                    "image/jpeg", "image",
                                                    it,
                                                )
                                                
                                            }
                                            println("icon3131 ${icon?.id}")
                                            
                                            val jsonContent = Json.encodeToString(
                                                buildJsonObject {
                                                    put("phoneNumber", phone.drop(1))
                                                    put("firstName", textState.value.firstName)
                                                    put("lastName", textState.value.lastName)
                                                    put("email", "admin.admin@gmail.com")
                                                    put("description", textState.value.firstName)
                                                    put("login", textState.value.nickname)
                                                    put("status", "active")
                                                    put("icon", icon?.id)
                                                }
                                            )
                                            
                                            println("jsonContent $jsonContent")
                                            
                                            val response: HttpResponse =
                                                client.post("${serverUrl}auth/sign-up") {
                                                    contentType(ContentType.Application.Json)
                                                    setBody(jsonContent)
                                                }
                                            println("responseresponse ${response.content}")
                                            
                                            if (response.status.isSuccess()) {
                                                
                                                val responseData: ReloadRes =
                                                    Json.decodeFromString(response.bodyAsText())
                                                
                                                
                                                addValueInStorage(
                                                    "accessToken",
                                                    responseData.accessToken
                                                )
                                                
                                                
                                                addValueInStorage(
                                                    "refreshToken",
                                                    responseData.refreshToken
                                                )
                                                
                                                
                                                
                                                viewModel.updateNotificationToken()
                                                
                                                viewModel.startObserving()
                                                
                                                сommonViewModel.cipherShared(
                                                    responseData.userId,
                                                    navigator
                                                )
                                                
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace() // It is a good practice to print the stack trace of the exception for debugging purposes
                                        } finally {
                                            client.close()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    fun TextFieldWithTitle(
        title: String,
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String
    ) {
        Column(
            modifier = Modifier.padding(top = 7.dp),
        ) {
            Text(
                title,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 15.sp,
                modifier = Modifier.padding(
                    top = 5.dp,
                    bottom = 8.dp,
                    start = 4.dp
                ),
                color = Color(0xFF000000)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder, style = TextStyle(
                                color = Color(0xFFC7C7C7),
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                lineHeight = 15.sp,
                            )
                        )
                    }
                    innerTextField()
                },
                textStyle = TextStyle(
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    lineHeight = 20.sp,
                    color = Color(0xFF000000)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .shadow(1.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth(0.9f).background(Color(0xFFFFFFFF))
                    .padding(start = 15.dp, top = 19.dp, bottom = 15.dp)
            )
        }
    }
}



