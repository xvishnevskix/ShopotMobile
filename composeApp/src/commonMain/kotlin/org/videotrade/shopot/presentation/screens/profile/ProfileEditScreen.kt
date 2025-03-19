package org.videotrade.shopot.presentation.screens.profile


import Avatar
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
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
import androidx.compose.ui.window.Dialog
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
import kotlinx.coroutines.CoroutineScope
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
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.ReloadRes
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PlatformFilePick
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.CustomTextField
import org.videotrade.shopot.presentation.components.Common.ModalDialogWithText
import org.videotrade.shopot.presentation.components.Common.ModalDialogWithoutText
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Common.validateDescription
import org.videotrade.shopot.presentation.components.Common.validateFirstName
import org.videotrade.shopot.presentation.components.Common.validateLastName
import org.videotrade.shopot.presentation.components.ProfileComponents.GroupEditHeader
import org.videotrade.shopot.presentation.components.ProfileComponents.ProfileEditHeader
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.add_photo
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.delete_account
import shopot.composeapp.generated.resources.human
import shopot.composeapp.generated.resources.pencil_in_circle


class ProfileEditScreen() : Screen {
    
    @Composable
    override fun Content() {
        val mainViewModel: MainViewModel = koinInject()
        val profileViewModel: ProfileViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        val profile = mainViewModel.profile.collectAsState(initial = ProfileDTO()).value
        val toasterViewModel: CommonViewModel = koinInject()
        val colors = MaterialTheme.colorScheme
        
        val navigator = LocalNavigator.currentOrThrow
        val textState = remember { mutableStateOf(profile.copy(
            firstName = profile.firstName,
            lastName = profile.lastName,
            login = profile.login,
            description = profile.description,
            phone = profile.phone
        )) }

        
        val scope = rememberCoroutineScope()
        val byteArray = remember { mutableStateOf<ByteArray?>(null) }
        var images by remember { mutableStateOf<ImageBitmap?>(null) }
        var image by remember { mutableStateOf<PlatformFilePick?>(null) }

        val firstNameError = remember { mutableStateOf<String?>(null) }
        val lastNameError = remember { mutableStateOf<String?>(null) }
        val nicknameError = remember { mutableStateOf<String?>(null) }
        val descriptionError = remember { mutableStateOf<String?>(null) }

        val nameValidate1 = stringResource(MokoRes.strings.name_is_required)
        val nameValidate2 = stringResource(MokoRes.strings.name_must_contain_only_letters)
        val nameValidate3 = stringResource(MokoRes.strings.name_must_not_contain_more_than_20_characters)
        val lastnameValidate1 = stringResource(MokoRes.strings.lastname_must_contain_only_letters)
        val lastnameValidate2 = stringResource(MokoRes.strings.lastname_must_not_contain_more_than_20_characters)
        val nickValidate1 = stringResource(MokoRes.strings.nickname_is_required)
        val nickValidate2 = stringResource(MokoRes.strings.nickname_must_contain_at_least_6_characters)
        val nickValidate3 = stringResource(MokoRes.strings.nickname_should_not_exceed_30_characters)
        val nickValidate4 = stringResource(MokoRes.strings.nickname_can_contain_only_letters_and_numbers)
        val deskValidate2 = stringResource(MokoRes.strings.the_description_must_not_exceed_40_characters)
        val fillInputs = stringResource(MokoRes.strings.please_fill_in_all_fields_correctly)

        val firstModalVisible = remember { mutableStateOf(false) }
        val secondModalVisible = remember { mutableStateOf(false) }
        val firstModalTitle = stringResource(MokoRes.strings.are_you_sure_you_want_to_delete_your_account)
        val secondModalText =  stringResource(MokoRes.strings.deleting_your_account_will_result_in_permanent_loss_of_all_data_continue)
        val secondModalTitle = stringResource(MokoRes.strings.attention)

        SafeArea(backgroundColor = colors.surface) {
            Column(modifier = Modifier.background(colors.surface)
                .imePadding()) {
                        ProfileEditHeader(stringResource(MokoRes.strings.edit_profile)) {
                        scope.launch {

                            if (firstNameError.value != null || lastNameError.value != null || descriptionError.value != null) {
                                toasterViewModel.toaster.show(
                                    fillInputs,
                                    type = ToastType.Error,
                                    duration = ToasterDefaults.DurationDefault
                                )
                            } else {
                                val profileUpdate = profileViewModel.sendNewProfile(
                                    textState.value,
                                    image,
                                    navigator
                                )

                                if (profileUpdate) {
                                    navigateToScreen(navigator,ProfileScreen(anotherUser = false))
                                }
                            }



                        }
                    }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.padding(top = 15.dp).fillMaxSize().verticalScroll(
                            rememberScrollState()
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                        ) {



                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        scope.launch {

                                            val filePick = FileProviderFactory.create()
                                                .pickFile(PickerType.Image)


                                            image = filePick


                                        }
                                    }
                                    .padding(top = 25.dp, bottom = 10.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                ,
                            ) {


                                if (image !== null) {
//                                Avatar(bitmap = images, size = 140.dp)

                                    val imagePainter =
                                        rememberAsyncImagePainter(image?.fileAbsolutePath)

                                    Surface(
                                        modifier = Modifier.size(128.dp),
                                        shape = CircleShape,
                                    ) {
                                        Image(
                                            painter = imagePainter,
                                            contentDescription = "Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(128.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .width(128.dp)
                                            .height(128.dp)
                                            .background(color = colors.surface)
                                        ,
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Avatar(icon = profile.icon, 128.dp, {
                                            scope.launch {

                                                val filePick = FileProviderFactory.create()
                                                    .pickFile(PickerType.Image)


                                                image = filePick


                                            }
                                        })
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
                                modifier = Modifier.fillMaxWidth().padding(top = 35.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CustomTextField(
                                    value = textState.value.firstName,
                                    onValueChange = {
                                        textState.value = textState.value.copy(firstName = it)
                                        firstNameError.value = validateFirstName(it, nameValidate1, nameValidate2, nameValidate3) // Валидация имени
                                    },
                                    placeholder = profile.firstName,
                                    error = firstNameError.value
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                CustomTextField(
                                    value = textState.value.lastName,
                                    onValueChange = {
                                        textState.value = textState.value.copy(lastName = it)
                                        lastNameError.value = validateLastName(it, lastnameValidate1, lastnameValidate2) // Валидация фамилии
                                    },
                                    placeholder = profile.lastName,
                                    error = lastNameError.value
                                )
                                Spacer(modifier = Modifier.height(12.dp))

//                            textState.value.login?.let {
//                                profile.login?.let { it1 ->
//                                    CustomTextField(
//                                        value = it,
//                                        onValueChange = {
//                                            textState.value = textState.value.copy(login = it)
//                                            nicknameError.value = validateNickname(it, nickValidate1, nickValidate2, nickValidate3, nickValidate4) // Валидация никнейма
//                                        },
//                                        placeholder = it1,
//                                        error = nicknameError.value
//                                        )
//                                }
//                            }
//                            Spacer(modifier = Modifier.height(28.dp))

                                profile.description?.let {
                                    textState.value.description?.let { it1 ->
                                        CustomTextField(
                                            title = stringResource(MokoRes.strings.description),
                                            value = it1,
                                            onValueChange = {
                                                textState.value = textState.value.copy(description = it)
                                                descriptionError.value = validateDescription(it, descValidate2 = deskValidate2) // Валидация описания
                                            },
                                            placeholder = it,
                                            error = descriptionError.value
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(80.dp))



                            }
                        }

                        Box(
                            modifier = Modifier.padding(top = 20.dp, bottom = 110.dp)
                        ) {
                            CustomButton(
                                stringResource(MokoRes.strings.delete_account),
                                {

                                    firstModalVisible.value = true
//                                    commonViewModel.mainNavigator.value?.let { mainViewModel.leaveApp(it) }
                                }, style = ButtonStyle.Red
                            )
                        }


                    }
                }

                if (firstModalVisible.value) {
                    ModalDialogWithoutText(
                        onDismiss = { firstModalVisible.value = false },
                        onConfirm = {
                            firstModalVisible.value = false
                            secondModalVisible.value = true
                        },
                        confirmText = stringResource(MokoRes.strings.delete),
                        dismissText = stringResource(MokoRes.strings.cancel),
                        title = firstModalTitle
                    )
                }

                if (secondModalVisible.value) {
                    ModalDialogWithText(
                        text = secondModalText,
                        onDismiss = { secondModalVisible.value = false },
                        onConfirm = {
                            scope.launch {
                                commonViewModel.mainNavigator.value?.let { mainViewModel.leaveApp(it, true) }
                            }
                        },
                        confirmText = stringResource(MokoRes.strings.delete_all),
                        dismissText = stringResource(MokoRes.strings.cancel),
                        title = secondModalTitle
                    )
                }
            }
        }
    }
}