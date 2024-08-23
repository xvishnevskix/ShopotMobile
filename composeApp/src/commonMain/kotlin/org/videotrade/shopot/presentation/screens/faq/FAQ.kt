import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Auth.Otp
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.videotrade.shopot.presentation.components.Auth.Otp
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import shopot.composeapp.generated.resources.Montserrat_SemiBold

import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold





suspend fun sendEmail(
    email: String,
    message: String
): HttpResponse? {
    val client = HttpClient(getHttpClientEngine()) {

    }

    try {
        val jsonContent = buildJsonObject {
            put("email", email)
            put("message", message)
        }.toString()

        println("Отправка email с данными: $jsonContent")

        val response: HttpResponse = client.post("https://oiweida.ru/api/user/mailSend") {
            contentType(ContentType.Application.Json)
            setBody(jsonContent)
        }

        println("Ответ от сервера: ${response.bodyAsText()}")

        if (response.status.isSuccess()) {
            return response
        } else {
            println("Ошибка при отправке письма: ${response.status.description}")
        }
    } catch (e: Exception) {
        println("Ошибка при выполнении запроса: $e")
    } finally {
        client.close()
    }

    return null
}


class FAQ() : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()


        val modalVisible = remember { mutableStateOf(false) }
        val isMessageSent = remember { mutableStateOf(false) }
        val loading = remember { mutableStateOf(false) }
        val isSuccessfulSend = remember { mutableStateOf(false) }
        val email = remember { mutableStateOf("") }
        val description = remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(10.dp)) {
                AuthHeader("FAQ", 0.55F)

                Spacer(modifier = Modifier.fillMaxHeight(0.05F))

                Text(
                    text = "Основные вопросы",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Column(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .fillMaxWidth()
                ) {
                    PolicyItem("Политика конфиденциальности") { /* Handle Click */ }
                    PolicyItem("Пользовательское соглашение") { /* Handle Click */ }
                    PolicyItem("Соглашение об обработке данных") { /* Handle Click */ }
                }

                Spacer(modifier = Modifier.fillMaxHeight(0.75F))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    CustomButton("Задать вопрос", {
                        modalVisible.value = true
                    })
                }

                if (modalVisible.value) {
                    ModalDialog(
                        onDismiss = {
                            modalVisible.value = false
                            isMessageSent.value = !isMessageSent.value
                                    },
                        email = email,
                        description = description,
                        isMessageSent = isMessageSent,
                        loading = loading,
                        isSuccessfulSend = isSuccessfulSend,
                        onSubmit = {
//                            if (email.value.isNotEmpty() && description.value.length >= 10) {
//                                coroutineScope.launch {
//                                    loading.value = true
//                                    val response = sendEmail(email.value, description.value)
//                                    loading.value = false
//                                    isSuccessfulSend.value = response != null && response.status.isSuccess()
//                                    isMessageSent.value = true
//                                }
//                            }
                            isMessageSent.value = true
                        }

                    )
                }
            }
        }
    }

    @Composable
    fun PolicyItem(text: String, onClick: () -> Unit) {
        Text(
            text = text,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(vertical = 7.dp)
                .padding(start = 10.dp),
            style = TextStyle(
                textDecoration = TextDecoration.Underline,
                fontSize = 16.sp,
                letterSpacing = (-0.5).sp
            )
        )
    }

    @Composable
    fun ModalDialog(
        onDismiss: () -> Unit,
        email: MutableState<String>,
        description: MutableState<String>,
        isMessageSent: MutableState<Boolean>,
        loading: MutableState<Boolean>,
        isSuccessfulSend: MutableState<Boolean>,
        onSubmit: () -> Unit
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        if (!isMessageSent.value) {
                            EmailInput(email)
                            Spacer(modifier = Modifier.height(10.dp))
                            DescriptionInput(description)
                            Spacer(modifier = Modifier.height(20.dp))

                            CustomButton("Отправить",
                                {   onSubmit()
                                }
                            )
                        } else if (loading.value) {
                            CircularProgressIndicator(color = Color.Red)
                        } else {
                            Text(
                                text = if (isSuccessfulSend.value) "✅ Ваше обращение успешно отправлено!" else "❌ Произошла ошибка, попробуйте снова",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 15.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EmailInput(email: MutableState<String>) {
        Column {
            Text(
                text = "Ваша электронная почта",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 11.dp)
            )
            BasicTextField(
                value = email.value,
                onValueChange = { email.value = it },
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (email.value.isEmpty()) {
                            Text(
                                "example@mail.ru",
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 20.sp,
                                color = Color(0xFF979797),
                                modifier = Modifier.padding()
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = Color(0xFF000000)
                ),
                modifier = Modifier
                    .shadow(3.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth()
                    .background(Color(0xFFFFFFFF))
                    .padding(10.dp)
            )
        }
    }

    @Composable
    fun DescriptionInput(description: MutableState<String>) {
        Column {
            Text(
                text = "Обращение",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 11.dp)
            )
            BasicTextField(
                value = description.value,
                onValueChange = { description.value = it },
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (description.value.isEmpty()) {
                            Text(
                                "Подробное описание поможет ответить вам как можно скорее",
                                textAlign = TextAlign.Start,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 20.sp,
                                color = Color(0xFF979797),
                                modifier = Modifier.padding()
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = Color(0xFF000000)
                ),
                modifier = Modifier
                    .height(200.dp)
                    .shadow(3.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth()
                    .background(Color(0xFFFFFFFF))
                    .padding(10.dp)
            )
        }
    }
}

