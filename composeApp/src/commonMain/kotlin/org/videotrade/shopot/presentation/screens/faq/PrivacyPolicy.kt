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

class PrivacyPolicy : Screen {


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
                    BaseHeader("Политика конфиденциальности мессенджера \"Шёпот\"", colors.surface)

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {
                        TextSection(
                            title = "Дата вступления в силу: 17.03.2025",
                            content = listOf(
                                "Данная Политика конфиденциальности описывает, как мы собираем, используем, храним и защищаем ваши данные, а также какие права вы имеете в отношении своей информации."
                            )
                        )

                        TextSection(
                            title = "1. Сбор информации",
                            content = listOf(
                                "Мы собираем только ту информацию, которая необходима для обеспечения бесперебойной работы мессенджера. Это может включать:",
                                "● Информацию, которую вы предоставляете: Имя пользователя, номер телефона, адрес электронной почты или другие данные, которые вы добровольно указываете при регистрации.",
                                "● Технические данные: IP-адрес, тип устройства, операционная система, время доступа и другие технические параметры, которые помогают нам улучшать качество сервиса.",
                                "● Данные об использовании: Мы можем собирать аналитическую информацию о том, как вы взаимодействуете с нашим сервисом (например, частота использования, количество отправленных сообщений и т.д.)."
                            )
                        )

                        TextSection(
                            title = "2. Использование информации",
                            content = listOf(
                                "Мы используем собранную информацию исключительно для следующих целей:",
                                "● Обеспечение функционирования мессенджера.",
                                "● Улучшение качества сервиса, исправление ошибок и разработка новых функций.",
                                "● Защита безопасности пользователей и предотвращение мошенничества.",
                                "● Соблюдение законодательства и ответ на запросы государственных органов (только в случаях, предусмотренных законом)."
                            )
                        )

                        TextSection(
                            title = "3. Защита данных",
                            content = listOf(
                                "Мы внедрили передовые технологии шифрования и многоуровневую систему защиты, чтобы обеспечить максимальную безопасность ваших данных. Тем не менее, как и в случае с любым цифровым сервисом, мы рекомендуем соблюдать базовые правила кибербезопасности для дополнительной защиты вашей учетной записи."
                            )
                        )

                        TextSection(
                            title = "4. Ответственность за утечки",
                            content = listOf(
                                "Мы предпринимаем все разумные усилия для защиты ваших данных, однако пользователи должны осознавать, что:",
                                "● Вы несете ответственность за безопасность своих учетных записей и данных. Мы рекомендуем использовать сложные пароли, двухфакторную аутентификацию и не передавать свои учетные данные третьим лицам.",
                                "● Мы не несем ответственности за утечки, вызванные действиями третьих лиц или пользователей. Например, если ваше устройство было взломано или вы сами передали данные злоумышленникам, мы не можем нести ответственность за такие инциденты.",
                                "● Мы не гарантируем сохранность данных в случае форс-мажорных обстоятельств, таких как хакерские атаки, стихийные бедствия или другие непредвиденные события."
                            )
                        )

                        TextSection(
                            title = "5. Права пользователей",
                            content = listOf(
                                "Вы имеете право:",
                                "● Запросить удаление вашей учетной записи и связанных с ней данных.",
                                "● Получить копию ваших данных, которые мы храним.",
                                "● Воспользоваться правом на забвение (удаление данных) в соответствии с применимым законодательством."
                            )
                        )

                        TextSection(
                            title = "6. Изменения в Политике конфиденциальности",
                            content = listOf(
                                "Мы оставляем за собой право вносить изменения в данную Политику конфиденциальности. Все изменения вступают в силу после публикации обновленной версии на нашем сайте или в приложении. Мы рекомендуем периодически проверять эту страницу для ознакомления с актуальной информацией."
                            )
                        )

                        TextSection(
                            title = "7. Контакты",
                            content = listOf(
                                "Если у вас есть вопросы или предложения по поводу данной Политики конфиденциальности, пожалуйста, свяжитесь с нами:",
                                "Электронная почта: shopotsupport@videotrade.ru"
                            )
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(10.dp).padding(bottom = 40.dp)
                ) {
                    BaseHeader("Privacy Policy of the Whisper Messenger", colors.surface)

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {
                        TextSection(
                            title = "Effective date: 17.03.2025",
                            content = listOf(
                                "This Privacy Policy describes how we collect, use, store and protect your data, as well as what rights you have with respect to your information."
                            )
                        )

                        TextSection(
                            title = "1. Collecting information",
                            content = listOf(
                                "We only collect information that is necessary to ensure the smooth operation of the messenger. This may include:",
                                "● Information that you provide: Your username, phone number, email address, or other information that you voluntarily provide during registration.",
                                "● Technical data: IP address, device type, operating system, access time, and other technical parameters that help us improve the quality of service.",
                                "● Usage Data: We may collect analytical information about how you interact with our service (for example, frequency of usage, number of messages sent, etc.)."
                            )
                        )

                        TextSection(
                            title = "2. Use of information",
                            content = listOf(
                                "We use the collected information exclusively for the following purposes:",
                                "● Ensuring the functioning of the messenger.",
                                "● Improve the quality of service, fix bugs, and develop new features.",
                                "● Protect user security and prevent fraud.",
                                "● Compliance with the law and responding to requests from government agencies (only in cases stipulated by law)."
                            )
                        )

                        TextSection(
                            title = "3. Data Protection",
                            content = listOf(
                                "We have implemented advanced encryption technologies and a multi-level security system to ensure maximum security of your data. However, as with any digital service, we recommend that you follow basic cybersecurity guidelines to further protect your account."
                            )
                        )

                        TextSection(
                            title = "4. Liability for leaks",
                            content = listOf(
                                "We make all reasonable efforts to protect your data, but users should be aware that:",
                                "● You are responsible for the security of your accounts and data. We recommend using complex passwords, two-factor authentication, and not sharing your credentials with third parties.",
                                "● We are not responsible for leaks caused by third parties or users. For example, if your device was hacked or you gave your data to intruders, we cannot be held responsible for such incidents.",
                                "● We do not guarantee the security of your data in case of force majeure, such as hacker attacks, natural disasters or other unforeseen events."
                            )
                        )

                        TextSection(
                            title = "5. User Rights",
                            content = listOf(
                                "You have the right to:",
                                "● Request deletion of your account and associated data.",
                                "● Get a copy of your data that we store.",
                                "● Exercise the right to be forgotten (data erasure) in accordance with applicable law."
                            )
                        )

                        TextSection(
                            title = "6. Changes to the Privacy Policy",
                            content = listOf(
                                "We reserve the right to make changes to this Privacy Policy. All changes will take effect after the updated version is published on our website or in the app. We recommend that you check this page periodically for up-to-date information."
                            )
                        )

                        TextSection(
                            title = "7. Contacts",
                            content = listOf(
                                "If you have any questions or suggestions about this Privacy Policy, please contact us:",
                                "Email address: shopotsupport@videotrade.ru"
                            )
                        )
                    }
                }

            }
        }

    }
}