package org.videotrade.shopot.presentation.screens.signUp

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.rememberAsyncImagePainter
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import dev.icerock.moko.resources.compose.stringResource
import io.github.vinceglb.filekit.core.PickerType
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
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ReloadRes
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PlatformFilePick
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.CustomTextField
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Common.validateFirstName
import org.videotrade.shopot.presentation.components.Common.validateLastName
import org.videotrade.shopot.presentation.components.Common.validateNickname
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.human
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
        val colors = MaterialTheme.colorScheme
        val scope = rememberCoroutineScope()
        val textState = remember { mutableStateOf(SignUpTextState()) }
        val byteArray = remember { mutableStateOf<ByteArray?>(null) }
        var images by remember { mutableStateOf<ImageBitmap?>(null) }
        val сommonViewModel: CommonViewModel = koinInject()
        var image by remember { mutableStateOf<PlatformFilePick?>(null) }
        val toasterViewModel: CommonViewModel = koinInject()
        val isLoading = remember { mutableStateOf(false) }

        val firstNameError = remember { mutableStateOf<String?>("") }
        val lastNameError = remember { mutableStateOf<String?>("") }
        val nicknameError = remember { mutableStateOf<String?>("") }

        val phoneIsRegistered = stringResource(MokoRes.strings.phone_number_is_already_registered)
        val fillInputs = stringResource(MokoRes.strings.please_fill_in_all_input_fields)
        val nameValidate1 = stringResource(MokoRes.strings.name_is_required)
        val nameValidate2 = stringResource(MokoRes.strings.name_must_contain_only_letters)
        val nameValidate3 =
            stringResource(MokoRes.strings.name_must_not_contain_more_than_20_characters)
        val lastnameValidate1 = stringResource(MokoRes.strings.lastname_must_contain_only_letters)
        val lastnameValidate2 =
            stringResource(MokoRes.strings.lastname_must_not_contain_more_than_20_characters)
        val nickValidate1 = stringResource(MokoRes.strings.nickname_is_required)
        val nickValidate2 =
            stringResource(MokoRes.strings.nickname_must_contain_at_least_6_characters)
        val nickValidate3 = stringResource(MokoRes.strings.nickname_should_not_exceed_30_characters)
        val nickValidate4 =
            stringResource(MokoRes.strings.nickname_can_contain_only_letters_and_numbers)

        SafeArea(padding = 4.dp, backgroundColor = colors.background) {
            Column(
                modifier = Modifier
                    .imePadding()
            ) {
                AuthHeader(stringResource(MokoRes.strings.create_account))

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.padding(top = 10.dp).fillMaxSize().verticalScroll(
                            rememberScrollState()
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,

                        ) {

                        Spacer(Modifier.height(40.dp))

                        Box(modifier = Modifier.clickable {
                            scope.launch {
                                val filePick = FileProviderFactory.create()
                                    .pickFile(PickerType.Image)


                                image = filePick

                            }


                        }) {


                            if (image !== null) {
//                                Avatar(bitmap = images, size = 140.dp)

                                val imagePainter =
                                    rememberAsyncImagePainter(image?.fileAbsolutePath)

                                Surface(
                                    modifier = Modifier.size(140.dp),
                                    shape = CircleShape,
                                ) {
                                    Image(
                                        painter = imagePainter,
                                        contentDescription = "Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(140.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .width(128.dp)
                                        .height(128.dp)
                                        .background(color = colors.onBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.human),
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(60.dp),
                                        colorFilter =  ColorFilter.tint(colors.primary)
                                    )
                                }
                                Image(
                                    painter = painterResource(Res.drawable.pencil_in_circle),
                                    contentDescription = "Edit",
                                    modifier = Modifier.size(24.dp).align(Alignment.BottomEnd),
                                    colorFilter =  ColorFilter.tint(colors.primary)
                                )
                            }


                        }



                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 35.dp).padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomTextField(
                                title = stringResource(MokoRes.strings.enter_your_name),
                                value = textState.value.firstName,
                                onValueChange = {
                                    textState.value = textState.value.copy(firstName = it)
                                    firstNameError.value = validateFirstName(
                                        it,
                                        nameValidate1,
                                        nameValidate2,
                                        nameValidate3
                                    ) // Валидация имени
                                },
                                placeholder = stringResource(MokoRes.strings.name),
                                error = firstNameError.value
                            )

                            CustomTextField(
                                title = stringResource(MokoRes.strings.enter_your_last_name),
                                value = textState.value.lastName,
                                onValueChange = {
                                    textState.value = textState.value.copy(lastName = it)
                                    lastNameError.value = validateLastName(
                                        it,
                                        lastnameValidate1,
                                        lastnameValidate2
                                    ) // Валидация фамилии
                                },
                                placeholder = stringResource(MokoRes.strings.lastname),
                                error = lastNameError.value
                            )

                            CustomTextField(
                                title = stringResource(MokoRes.strings.come_up_with_a_nickname),
                                value = textState.value.nickname,
                                onValueChange = {
                                    textState.value = textState.value.copy(nickname = it)
                                    nicknameError.value = validateNickname(
                                        it,
                                        nickValidate1,
                                        nickValidate2,
                                        nickValidate3,
                                        nickValidate4
                                    ) // Валидация никнейма
                                },
                                placeholder = stringResource(MokoRes.strings.come_up_nickname),
                                error = nicknameError.value
                            )
                            Spacer(modifier = Modifier.height(40.dp))


                        }


                        Box(
                            modifier = Modifier.padding(bottom = 80.dp)
                        ) {
                            CustomButton(
                                stringResource(MokoRes.strings.create_account),
                                { scope ->
                                    if (firstNameError.value != null || lastNameError.value != null || nicknameError.value != null) {
                                        toasterViewModel.toaster.show(
                                            fillInputs,
                                            type = ToastType.Error,
                                            duration = ToasterDefaults.DurationDefault
                                        )
                                    } else {
                                        scope.launch {
                                            val client = HttpClient(getHttpClientEngine())
                                            isLoading.value = true
                                            try {
                                                val icon = image?.let {
                                                    origin().sendImageFile(
                                                        image!!.fileAbsolutePath,
                                                        "image", image!!.fileName,
                                                        true
                                                    )

                                                }
                                                println("phone ${phone}")
////
//                                            return@launch
                                                val jsonContent = Json.encodeToString(
                                                    buildJsonObject {
                                                        put("phoneNumber", phone.drop(1))
                                                        put("firstName", textState.value.firstName)
                                                        put("lastName", textState.value.lastName)
                                                        put("email", "admin.admin@gmail.com")
                                                        put(
                                                            "description",
                                                            textState.value.firstName
                                                        )
                                                        put("login", textState.value.nickname)
                                                        put("status", "active")
                                                        put("icon", icon)
                                                    }
                                                )

                                                println("jsonContent $jsonContent")


                                                val response: HttpResponse =
                                                    client.post("${SERVER_URL}auth/sign-up") {
                                                        contentType(ContentType.Application.Json)
                                                        setBody(jsonContent)
                                                    }


                                                println("responseresponse ${response.bodyAsText()}")

                                                if (response.status.value == 500) {
                                                    toasterViewModel.toaster.show(
                                                        phoneIsRegistered,
                                                        type = ToastType.Warning,
                                                        duration = ToasterDefaults.DurationDefault
                                                    )
                                                }

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

                                                    responseData.deviceId?.let {
                                                        addValueInStorage(
                                                            "deviceId",
                                                            it
                                                        )
                                                    }

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
                                                isLoading.value = false
                                                client.close()
                                            }
                                        }
                                    }
                                }, style = ButtonStyle.Gradient,
                                isLoading = isLoading.value
                            )
                        }

                    }
                }
            }
        }
    }




}