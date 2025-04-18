import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.TextSection
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold

class UserAgreement : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val colors = MaterialTheme.colorScheme
        val storedLanguage = getValueInStorage("selected_language")
        Box(
            modifier = Modifier
                .fillMaxSize().background(colors.surface)
        ) {
            if (storedLanguage == "ru") {
                Column(
                    modifier = Modifier.padding(10.dp).padding(bottom = 40.dp)
                ) {
                    BaseHeader("Пользовательское соглашение мессенджера \"Шёпот\"", colors.surface)

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {
                        TextSection(
                            title = "Дата вступления в силу: 17.03.2025",
                            content = listOf(
                                "Данное Пользовательское соглашение (далее — \"Соглашение\") регулирует использование вами нашего сервиса, включая все связанные функции, приложения и услуги. Используя наш мессенджер, вы автоматически принимаете условия данного Соглашения. Если вы не согласны с какими-либо из его положений, пожалуйста, прекратите использование сервиса."
                            )
                        )

                        TextSection(
                            title = "1. Прием условий",
                            content = listOf(
                                "Использование мессенджера \"Шёпот\" означает ваше полное согласие с данным Соглашением, а также с нашей Политикой конфиденциальности. Мы оставляем за собой право вносить изменения в данное Соглашение. Все изменения вступают в силу после их публикации на нашем сайте или в приложении. Ваше дальнейшее использование сервиса после внесения изменений считается принятием обновленных условий."
                            )
                        )

                        TextSection(
                            title = "2. Условия использования",
                            content = listOf(
                                "Вы можете использовать наш мессенджер только в личных и законных целях. Вы обязуетесь:",
                                "● Не нарушать права других пользователей или третьих лиц.",
                                "● Не использовать сервис для распространения незаконного, вредоносного, оскорбительного, клеветнического или иного контента, который может быть расценен как противоправный.",
                                "● Не создавать угрозы безопасности, включая попытки взлома, распространение вредоносного программного обеспечения или другие действия, направленные на нарушение работы сервиса.",
                                "● Не передавать свои учетные данные третьим лицам."
                            )
                        )

                        TextSection(
                            title = "3. Регистрация и учетная запись",
                            content = listOf(
                                "Для использования некоторых функций мессенджера вам может потребоваться регистрация. При регистрации вы обязуетесь:",
                                "● Предоставить точную и актуальную информацию.",
                                "● Обеспечивать конфиденциальность своих учетных данных.",
                                "● Немедленно сообщать нам о любом несанкционированном доступе к вашей учетной записи."
                            )
                        )

                        TextSection(
                            title = "4. Права на контент",
                            content = listOf(
                                "4.1. Ваш контент: Вы сохраняете все права на контент, который вы создаете и отправляете через наш мессенджер. Однако, предоставляя контент, вы даете нам ограниченное, неисключительное право использовать его исключительно для обеспечения работы сервиса.",
                                "4.2. Контент других пользователей: Вы понимаете и соглашаетесь, что вы можете столкнуться с контентом других пользователей, который может быть неприемлемым или противоречащим вашим взглядам. Мы не несем ответственности за такой контент."
                            )
                        )

                        TextSection(
                            title = "5. Ограничение ответственности",
                            content = listOf(
                                "Мы предпринимаем все возможные меры для обеспечения бесперебойной работы сервиса, но не можем гарантировать:",
                                "● Абсолютную защиту от внешних угроз.",
                                "● Сохранность данных в случае форс-мажорных обстоятельств.",
                                "● Отсутствие временных сбоев в работе сервиса."
                            )
                        )

                        TextSection(
                            title = "6. Запрещенные действия",
                            content = listOf(
                                "Вы не имеете права:",
                                "● Использовать мессенджер для незаконной деятельности.",
                                "● Распространять спам или вредоносный контент.",
                                "● Нарушать права интеллектуальной собственности.",
                                "● Пытаться получить несанкционированный доступ к данным других пользователей."
                            )
                        )

                        TextSection(
                            title = "7. Прекращение использования",
                            content = listOf(
                                "Мы оставляем за собой право приостановить или прекратить ваш доступ к мессенджеру, если вы нарушаете условия данного Соглашения."
                            )
                        )

                        TextSection(
                            title = "8. Юридическая информация",
                            content = listOf(
                                "8.1. Данное Соглашение регулируется законодательством Российской Федерации.",
                                "8.2. Любые споры должны быть урегулированы путем переговоров, а при невозможности — переданы в суд."
                            )
                        )

                        TextSection(
                            title = "9. Контакты",
                            content = listOf(
                                "Если у вас возникли вопросы или предложения, пожалуйста, свяжитесь с нами по электронной почте: shopotsupport@videotrade.ru."
                            )
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(10.dp).padding(bottom = 40.dp)
                ) {
                    BaseHeader("User Agreement of the Whisper Messenger", colors.surface)

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {
                        TextSection(
                            title = "Effective date: 17.03.2025",
                            content = listOf(
                                "This User Agreement (hereinafter referred to as the \"Agreement\") regulates your use of our service, including all related features, applications, and services. By using our messenger, you automatically accept the terms of this Agreement. If you do not agree with any of its provisions, please stop using the service."
                            )
                        )

                        TextSection(
                            title = "1. Acceptance of terms and conditions",
                            content = listOf(
                                "Using the Whisper messenger means that you fully agree to this Agreement, as well as to our Privacy Policy. We reserve the right to make changes to this Agreement. All changes will take effect after they are published on our website or in the app. Your continued use of the Service after the changes are made is considered acceptance of the updated terms."
                            )
                        )

                        TextSection(
                            title = "2. Terms of Use",
                            content = listOf(
                                "You can only use our messenger for personal and legitimate purposes. You agree to:",
                                "● Do not violate the rights of other users or third parties.",
                                "● Do not use the service to distribute illegal, malicious, offensive, slanderous, or other content that may be considered illegal.",
                                "● Do not create security threats, including hacking attempts, distribution of malicious software, or other actions aimed at disrupting the service.",
                                "● Do not share your credentials with third parties."
                            )
                        )

                        TextSection(
                            title = "3. Registration and account",
                            content = listOf(
                                "You may need to register to use some of the messenger's features. When registering, you agree to:",
                                "● Provide accurate and up-to-date information.",
                                "● Ensure the confidentiality of your credentials.",
                                "● Immediately notify us of any unauthorized access to your account."
                            )
                        )

                        TextSection(
                            title = "4. Content Rights",
                            content = listOf(
                                "4.1. Your content: You retain all rights to the content that you create and send via our messenger. However, by providing the content, you grant us a limited, non-exclusive right to use it exclusively for the operation of the service.",
                                "4.2. Content of other users: You understand and agree that you may encounter content from other users that may be inappropriate, offensive, or contrary to your views. We are not responsible for such content."
                            )
                        )

                        TextSection(
                            title = "5. Limitation of Liability",
                            content = listOf(
                                "We take all possible measures to ensure the smooth operation of the service, but we cannot guarantee:",
                                "● Absolute protection from external threats.",
                                "● Data security in case of force majeure.",
                                "● No temporary service failures caused by technical updates or maintenance."
                            )
                        )

                        TextSection(
                            title = "6. Prohibited actions",
                            content = listOf(
                                "You may not:",
                                "● Use the messenger for illegal activities.",
                                "● Distribute spam, phishing links, or other malicious content.",
                                "● Infringe the intellectual property rights of others.",
                                "● Attempt to gain unauthorized access to other users' systems or data."
                            )
                        )

                        TextSection(
                            title = "7. Discontinuation of use",
                            content = listOf(
                                "We reserve the right to suspend or terminate your access to the messenger if you violate the terms of this Agreement."
                            )
                        )

                        TextSection(
                            title = "8. Legal information",
                            content = listOf(
                                "8.1. This Agreement is governed by and interpreted in accordance with the laws of the Russian Federation.",
                                "8.2. Any disputes must be settled through negotiations, and if not possible, submitted to the court."
                            )
                        )

                        TextSection(
                            title = "9. Contacts",
                            content = listOf(
                                "If you have any questions or suggestions about this Agreement, please contact us at: shopotsupport@videotrade.ru."
                            )
                        )
                    }
                }
            }
        }
    }
}