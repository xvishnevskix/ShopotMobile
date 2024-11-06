import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold

import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold
import shopot.composeapp.generated.resources.auth_logo


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

        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

            if (modalVisible.value) {
                SupportModalDialog(
                    onDismiss = {
                        modalVisible.value = false
                        isMessageSent.value = false
                    },
                    email = email,
                    description = description,
                    isMessageSent = isMessageSent,
                    loading = loading,
                    isSuccessfulSend = isSuccessfulSend,
                    onSubmit = {
                        if (email.value.isNotEmpty() && description.value.length >= 3) {
                            coroutineScope.launch {
                                loading.value = true
                                val response = sendEmail(email.value, description.value)
                                loading.value = false
                                isSuccessfulSend.value =
                                    response != null && response.status.isSuccess()
                                isMessageSent.value = true
                            }
                        }
//                            isMessageSent.value = true
                    }

                )
            }

            Column(
                modifier = Modifier.padding(10.dp).fillMaxHeight(0.9f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    BaseHeader(stringResource(MokoRes.strings.support))

                    Spacer(modifier = Modifier.fillMaxHeight(0.05F))

                    Text(
                        stringResource(MokoRes.strings.main_questions),
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        textAlign = TextAlign.Center,
                        color = Color(0xFF373533),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                            .fillMaxWidth()
                    ) {
                        PolicyItem(stringResource(MokoRes.strings.privacy_policy)) {
                            navigator.push(PrivacyPolicy())
                        }
                        PolicyItem(stringResource(MokoRes.strings.user_agreement)) {
                            navigator.push(UserAgreement())
                        }
                        PolicyItem(stringResource(MokoRes.strings.data_processing_agreement)) {
                            navigator.push(DataProcessingAgreement())
                        }
                    }
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()

                        .safeDrawingPadding(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    CustomButton(stringResource(MokoRes.strings.ask_question), {
                        modalVisible.value = true
                    }, style = ButtonStyle.Gradient)
                }


            }
        }
    }

    @Composable
    fun PolicyItem(text: String, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    width = 1.dp,
                    color = Color(0x33373533),
                    shape = RoundedCornerShape(size = 16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 20.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF373533),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                )
            )
        }
    }

    @Composable
    fun SupportModalDialog(
        onDismiss: () -> Unit,
        email: MutableState<String>,
        description: MutableState<String>,
        isMessageSent: MutableState<Boolean>,
        loading: MutableState<Boolean>,
        isSuccessfulSend: MutableState<Boolean>,
        onSubmit: () -> Unit
    ) {
        var isEmailValid by remember { mutableStateOf(true) }
        var isDescValid by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Dialog(onDismissRequest = onDismiss) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(5.dp)
                            .fillMaxWidth()
                    ) {
                        if (!isMessageSent.value) {
                            Row(
                                modifier = Modifier.clickable { onDismiss() }.fillMaxWidth().padding(5.dp),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                Icon(

                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFF000000),
                                    modifier = Modifier
                                        .size(15.dp)


                                )
                            }
                            Column(
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                EmailInput(email, isEmailValid)
                                Spacer(modifier = Modifier.height(10.dp))
                                DescriptionInput(description, isDescValid)
                                Spacer(modifier = Modifier.height(20.dp))

                                CustomButton(stringResource(MokoRes.strings.send),
                                    {
                                        isEmailValid = validateEmail(email.value)
                                        isDescValid = validateDescription(description.value)
                                        if (isEmailValid && isDescValid) {
                                            onSubmit()
                                        }
                                    }
                                    , style = ButtonStyle.Gradient
                                )
                            }
                        } else if (loading.value) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp, bottom = 20.dp)
                            ) {
                                CircularProgressIndicator(color = Color(0xFF979797))
                            }
                        } else {

                            if (isSuccessfulSend.value) {
                                Column(
                                    modifier = Modifier.width(324.dp)
                                        .height(324.dp)
                                        .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 16.dp))
                                        ,
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .size(width = 128.dp, height = 86.dp),
                                        painter = painterResource(Res.drawable.auth_logo),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text(
                                        stringResource(
                                            MokoRes.strings.request_accepted
                                        ),
                                        fontSize = 20.sp,
                                        lineHeight = 20.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                        fontWeight = FontWeight(500),
                                        textAlign = TextAlign.Center,
                                        color = Color(0xFF373533),
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        stringResource(
                                            MokoRes.strings.we_will_contact_you_shortly_and_try_to_solve_the_problem
                                        ),
                                        fontSize = 15.sp,
                                        lineHeight = 15.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        textAlign = TextAlign.Center,
                                        color = Color(0x80373533),
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        maxLines = 3,
                                    )

                                }
                            }
                            else {
                                Text(
                                    text = stringResource(
                                        MokoRes.strings.an_error_occurred_please_try_again
                                    ),
                                    fontSize = 14.sp,
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    modifier = Modifier.padding(vertical = 15.dp)
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EmailInput(email: MutableState<String>, isEmailValid: Boolean) {
        Column {
            Text(
                stringResource(MokoRes.strings.your_email),
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center,
                color = Color(0xFF373533),
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            BasicTextField(
                value = email.value,
                onValueChange = { email.value = it },
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (email.value.isEmpty()) {
                            Text(
                                "example@mail.ru",
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                textAlign = TextAlign.Start,
                                color = Color(0x80373533),
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Start,
                    color = Color(0xFF373533)
                ),
                modifier = Modifier
                    .border(width = 1.dp, color = Color(0x33373533), shape = RoundedCornerShape(size = 16.dp))
                    .fillMaxWidth(1f).background(Color(0xFFFFFFFF))
                    .padding(start = 16.dp, top = 20.dp, bottom = 20.dp)
            )
            if (!isEmailValid) {
                Text(
                    stringResource(MokoRes.strings.please_enter_a_valid_email),
                    color = Color.Red,
                    fontSize = 12.sp,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }

    @Composable
    fun DescriptionInput(description: MutableState<String>, isDescValid: Boolean) {
        Column {
            Text(
                stringResource(MokoRes.strings.appeal),
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center,
                color = Color(0xFF373533),
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            BasicTextField(
                value = description.value,
                onValueChange = { description.value = it },
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (description.value.isEmpty()) {
                            Text(
                                stringResource(MokoRes.strings.detailed_description_will_help_us_answer_you_as_soon_as_possible),
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                textAlign = TextAlign.Start,
                                color = Color(0x80373533),
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Start,
                    color = Color(0xFF373533)
                ),
                modifier = Modifier
                    .border(width = 1.dp, color = Color(0x33373533), shape = RoundedCornerShape(size = 16.dp))
                    .fillMaxWidth(1f).height(232.dp).background(Color(0xFFFFFFFF))
                    .padding(start = 16.dp, top = 16.dp, bottom = 20.dp)
            )
            if (!isDescValid) {
                Text(
                    text = stringResource(MokoRes.strings.don_not_forget_to_describe_the_problem),
                    color = Color.Red,
                    fontSize = 12.sp,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }
}

fun validateEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return emailRegex.matches(email)
}

fun validateDescription(description: String): Boolean {
    return description.length >= 3
}