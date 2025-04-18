import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
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
            put("message", "$message  \nApp Version: 1.1.5(beta) \n${getPlatform().name}")
        }.toString()

        println("Sending email with data: $jsonContent")

        val response: HttpResponse = client.post("${EnvironmentConfig.SERVER_URL}user/mailSend") {
            contentType(ContentType.Application.Json)
            setBody(jsonContent)
        }

        println("Response from the server user/mailSend: ${response.bodyAsText()}")

        if (response.status.isSuccess()) {
            return response
        } else {
            println("Error sending email: ${response.status.description}")
        }
    } catch (e: Exception) {
        println("Error executing request: $e")
    } finally {
        client.close()
    }

    return null
}


class FAQ : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()

        val colors = MaterialTheme.colorScheme
        val modalVisible = remember { mutableStateOf(false) }
        val isMessageSent = remember { mutableStateOf(false) }
        val loading = remember { mutableStateOf(false) }
        val isSuccessfulSend = remember { mutableStateOf(false) }
        val email = remember { mutableStateOf("") }
        val description = remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize().background(colors.surface)) {



            Column(
                modifier = Modifier.padding(10.dp).fillMaxHeight(0.9f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    BaseHeader(stringResource(MokoRes.strings.support), colors.surface)

                    Spacer(modifier = Modifier.fillMaxHeight(0.05F))

                    Text(
                        stringResource(MokoRes.strings.main_questions),
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        textAlign = TextAlign.Center,
                        color = colors.primary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                            .fillMaxWidth()
                    ) {
                        PolicyItem(stringResource(MokoRes.strings.privacy_policy)) {
                            navigateToScreen(navigator,PrivacyPolicy())
                        }
                        PolicyItem(stringResource(MokoRes.strings.user_agreement)) {
                            navigateToScreen(navigator,UserAgreement())
                        }
                        PolicyItem(stringResource(MokoRes.strings.data_processing_agreement)) {
                            navigateToScreen(navigator,DataProcessingAgreement())
                        }
                    }
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()

                        ,
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        CustomButton(stringResource(MokoRes.strings.ask_question), {
                            modalVisible.value = true
                        }, style = ButtonStyle.Gradient)
                    }

                    Box(
                        modifier = Modifier
                            .safeDrawingPadding()
                            .padding()
                    ) {

                        Text(
                            text = "App Version: 1.1.5(beta)",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                textAlign = TextAlign.Center,
                                color = colors.secondary,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            )
                        )
                    }
                }


            }
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
        }
    }

    @Composable
    fun PolicyItem(text: String, onClick: () -> Unit) {
        val colors = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = colors.secondaryContainer,
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                modifier = Modifier

                    .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 20.dp),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center,
                    color = colors.primary,
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
        val colors = MaterialTheme.colorScheme

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
                    LazyColumn(
                        modifier = Modifier
                            .background(colors.background)
                            .padding(5.dp)
                            .fillMaxWidth()
                    ) {
                        item {
                            if (!isMessageSent.value) {
                                Row(
                                    modifier = Modifier.clickable { onDismiss() }.fillMaxWidth().padding(5.dp),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    Icon(

                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = colors.primary,
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
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            } else if (loading.value) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 20.dp, bottom = 20.dp)
                                ) {
                                    CircularProgressIndicator(color = colors.surface)
                                }
                            } else {

                                if (isSuccessfulSend.value) {

                                    email.value = ""
                                    description.value = ""
                                    Column(
                                        modifier = Modifier.width(324.dp)
                                            .height(324.dp)
                                            .background(color = colors.background, shape = RoundedCornerShape(size = 16.dp))
                                        ,
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .size(width = 128.dp, height = 86.dp),
                                            painter = painterResource(Res.drawable.auth_logo),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            colorFilter =  ColorFilter.tint(colors.primary)
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
                                            color = colors.primary,
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
                                            color = colors.secondary,
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
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        color = colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        modifier = Modifier.padding(vertical = 15.dp, horizontal = 16.dp)
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EmailInput(email: MutableState<String>, isEmailValid: Boolean) {
        val colors = MaterialTheme.colorScheme
        Column {
            Text(
                stringResource(MokoRes.strings.your_email),
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center,
                color = colors.primary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            BasicTextField(
                cursorBrush = SolidColor(colors.primary),
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
                                color = colors.secondary,
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
                    color = colors.primary
                ),
                modifier = Modifier
                    .border(width = 1.dp, color = colors.secondaryContainer, shape = RoundedCornerShape(size = 16.dp))
                    .fillMaxWidth(1f).background(colors.background)
                    .padding(start = 16.dp, top = 20.dp, bottom = 20.dp)
            )
            if (!isEmailValid) {
                Text(
                    stringResource(MokoRes.strings.please_enter_a_valid_email),
                    color = colors.error,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }


}

@Composable
fun DescriptionInput(description: MutableState<String>, isDescValid: Boolean) {
    val colors = MaterialTheme.colorScheme
    Column {
        Text(
            stringResource(MokoRes.strings.description),
            fontSize = 16.sp,
            lineHeight = 16.sp,
            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
            fontWeight = FontWeight(500),
            textAlign = TextAlign.Center,
            color = colors.primary,
            letterSpacing = TextUnit(0F, TextUnitType.Sp),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        BasicTextField(
            cursorBrush = SolidColor(colors.primary),
            value = description.value,
            onValueChange = { description.value = it },
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (description.value.isEmpty()) {
                        Text(
                            stringResource(MokoRes.strings.detailed_description_will_help_improve_our_application),
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            textAlign = TextAlign.Start,
                            color = colors.secondary,
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
                color = colors.primary
            ),
            modifier = Modifier
                .border(width = 1.dp, color = colors.secondaryContainer, shape = RoundedCornerShape(size = 16.dp))
                .fillMaxWidth(1f).height(232.dp).background(colors.background)
                .padding(start = 16.dp, top = 16.dp, bottom = 20.dp, end = 16.dp)
        )
        if (!isDescValid) {
            Text(
                text = stringResource(MokoRes.strings.don_not_forget_to_describe_the_problem),
                color = colors.error,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                modifier = Modifier.padding(top = 5.dp)
            )
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
