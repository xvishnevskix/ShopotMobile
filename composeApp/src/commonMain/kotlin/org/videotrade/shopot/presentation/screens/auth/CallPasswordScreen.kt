package org.videotrade.shopot.presentation.screens.auth

import PrivacyPolicy
import UserAgreement
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.multiplatform.callInPhone
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import org.videotrade.shopot.presentation.screens.login.CountryName
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.auth_logo
//import shopot.composeapp.generated.resources.call_password_success

class CallPasswordScreen(
    private val phone: String,
    private val authCase: String,
    private val countryName: CountryName
) : Screen {
    
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val colors = MaterialTheme.colorScheme
        val authViewModel: AuthViewModel = koinInject()
        
        val responseState = remember { mutableStateOf<String?>("1111") }
        val otpFields = remember { mutableStateListOf("", "", "", "") }
        val isSuccessOtp = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val viewModel: IntroViewModel = koinInject()
        val сommonViewModel: CommonViewModel = koinInject()
        val toasterViewModel: CommonViewModel = koinInject()
        var time by remember { mutableStateOf(30) }
        var isRunning by remember { mutableStateOf(false) }
        var reloadSend by remember { mutableStateOf(false) }
        var isSmsMode by remember { mutableStateOf(false) }
        var isSms by remember { mutableStateOf(false) }
        
        
        val isLoading = remember { mutableStateOf(false) }
        
        val phoneNotRegistered = stringResource(MokoRes.strings.phone_number_is_not_registered)
        val invalidCode = stringResource(MokoRes.strings.invalid_code)
        val sentSMSCode = stringResource(MokoRes.strings.sms_with_code_sent)
        val serverUnavailable =
            stringResource(MokoRes.strings.the_server_is_temporarily_unavailable)
        var hasError = remember { mutableStateOf(false) }
        val animationTrigger = remember { mutableStateOf(false) }
        
        val isCallPasswordSuccess by authViewModel.isCallPasswordSuccess.collectAsState()
        val confirmNumber by authViewModel.confirmNumber.collectAsState()
        val callId by authViewModel.callId.collectAsState()
        
        val scrollState = rememberScrollState()
        
        
        
        DisposableEffect(Unit) {
            onDispose {
                authViewModel.isCallPasswordSuccess.value = false
                authViewModel.confirmNumber.value = ""
            }
        }
        
        
        
        LaunchedEffect(key1 = Unit) {
            viewModel.navigator.value = navigator
            
            when (authCase) {
                "SignIn" -> {
                    if (phone == "+79990000000") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                            serverUnavailable,
                        )
                    }
                    if (phone == "+79899236226") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                            serverUnavailable,
                        )
                    }
                    if (phone == "+79388899885") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                            serverUnavailable,
                        )
                    }
                    if (phone == "+375336483673") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                            serverUnavailable,
                        )
                    }
                }
                
                "SignUp" -> {
                    if (phone == "+79990000000") {
                        sendSignUp(phone, navigator)
                    }
                    if (phone == "+79899236226") {
                        sendSignUp(phone, navigator)
                    }
                    if (phone == "+79388899885") {
                        sendSignUp(phone, navigator)
                    }
                    if (phone == "+375336483673") {
                        sendSignUp(phone, navigator)
                    }
                }
            }
            
        
        }
        
        
        
        
        fun handleError(errorMessage: String) {
            hasError.value = true
            isLoading.value = false
            animationTrigger.value = !animationTrigger.value
            toasterViewModel.toaster.show(
                message = errorMessage,
                type = ToastType.Warning,
                duration = ToasterDefaults.DurationDefault,
            )
        }
        
        suspend fun handleAuthCase() {
            when (authCase) {
                "SignIn" -> sendLogin(
                    phone,
                    navigator,
                    viewModel,
                    сommonViewModel = сommonViewModel,
                    toasterViewModel = toasterViewModel,
                    phoneNotRegistered = phoneNotRegistered,
                    serverUnavailable = serverUnavailable,
                )
                
                "SignUp" -> sendSignUp(phone, navigator)
            }
        }
        
        
        LaunchedEffect(key1 = isCallPasswordSuccess) {
            if (isCallPasswordSuccess) {
                handleAuthCase()
            }
        }
        
        
        
        SafeArea(padding = 4.dp, backgroundColor = colors.background) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .imePadding()
            ) {
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max) // Позволяет контенту расти, а не умещаться в экран
                        .verticalScroll(scrollState), // Включаем скроллинг
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (authCase) {
                        "SignIn" -> AuthHeader(stringResource(MokoRes.strings.login))
                        "SignUp" -> AuthHeader(stringResource(MokoRes.strings.create_account))
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Image(
                        modifier = Modifier.size(width = 195.dp, height = 132.dp),
                        painter = painterResource(Res.drawable.auth_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(colors.primary)
                    )
                    
                    Spacer(modifier = Modifier.height(50.dp))
                    
                    if (!isCallPasswordSuccess) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Подтвердите номер",
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                    fontWeight = FontWeight(500),
                                    textAlign = TextAlign.Center,
                                    color = colors.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "Для подтверждения номера позвоните по номеру:",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                    fontWeight = FontWeight(400),
                                    textAlign = TextAlign.Center,
                                    color = colors.secondary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                confirmNumber,
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                    fontWeight = FontWeight(500),
                                    textAlign = TextAlign.Center,
                                    color = colors.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "Звонок бесплатный. Он будет сброшен, а система подтвердит вход",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                    fontWeight = FontWeight(400),
                                    textAlign = TextAlign.Center,
                                    color = colors.secondary
                                )
                            )
                            Spacer(modifier = Modifier.height(50.dp))
                            
                            Box(modifier = Modifier.padding(bottom = 20.dp)) {
                                CustomButton(
                                    text = "Позвонить",
                                    { scope ->
                                        scope.launch {
                                            callInPhone(confirmNumber)
                                        }
                                    },
                                    style = ButtonStyle.Gradient,
                                    disabled = isRunning
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(70.dp))
                            
                            CustomButton(
                                text = "Другие способы входа",
                                { scope ->
                                    scope.launch {
                                        navigateToScreen(
                                            navigator,
                                            AuthCallScreen(phone, authCase, countryName)
                                        )
                                    }
                                },
                                style = ButtonStyle.Primary,
                                disabled = isRunning
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    } else {
                        Spacer(modifier = Modifier.height(40.dp))
//                        Image(
//                            modifier = Modifier.size(width = 195.dp, height = 146.dp),
//                            painter = painterResource(Res.drawable.call_password_success),
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop
//                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            "Номер успешно подтверждён!",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                fontWeight = FontWeight(500),
                                textAlign = TextAlign.Center,
                                color = colors.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Через 5 сек. вы войдёте в аккаунт",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                textAlign = TextAlign.Center,
                                color = colors.secondary
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))


                    
                    AgreementText(
                        onUserAgreementClick = { navigateToScreen(navigator, UserAgreement()) },
                        onPrivacyPolicyClick = { navigateToScreen(navigator, PrivacyPolicy()) }
                    )
                    
                    
                }
            }
        }
    }
}


@Composable
fun AgreementText(
    onUserAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    val textColor = Color(0x37353380) // Обычный цвет текста
    val linkColor = Color(0x80353380) // Цвет для ссылок
    
    val annotatedString = remember {
        buildAnnotatedString {
            append("Продолжая вход, вы соглашаетесь с ")
            
            val startUserAgreement = length
            append("пользовательским соглашением")
            addStyle(
                SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                ),
                start = startUserAgreement,
                end = length
            )
            addStringAnnotation("USER_AGREEMENT", "user_agreement", startUserAgreement, length)
            
            append(" и ")
            
            val startPrivacyPolicy = length
            append("соглашением о конфиденциальности")
            addStyle(
                SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                ),
                start = startPrivacyPolicy,
                end = length
            )
            addStringAnnotation("PRIVACY_POLICY", "privacy_policy", startPrivacyPolicy, length)
        }
    }
    
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    
    BasicText(
        text = annotatedString,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp)
            .wrapContentWidth(Alignment.CenterHorizontally) // Центрирование текста
            .wrapContentHeight(Alignment.CenterVertically)
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    textLayoutResult?.let { layoutResult ->
                        val position = layoutResult.getOffsetForPosition(tapOffset)
                        annotatedString.getStringAnnotations(position, position)
                            .firstOrNull()?.let { annotation ->
                                when (annotation.item) {
                                    "user_agreement" -> onUserAgreementClick()
                                    "privacy_policy" -> onPrivacyPolicyClick()
                                }
                            }
                    }
                }
            }, // Если нужно центрирование по высоте
        onTextLayout = { textLayoutResult = it },
        style = TextStyle(
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
            fontWeight = FontWeight.W600, // Более плотный текст
            textAlign = TextAlign.Center, // Центрирование текста внутри строки
            letterSpacing = (-0.5).sp, // Более плотный текст
            color = Color(0x37353380)
        )
    )
    
}


