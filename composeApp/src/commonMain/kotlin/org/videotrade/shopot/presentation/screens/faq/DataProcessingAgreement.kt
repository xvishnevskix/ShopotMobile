import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.parkingProj.presentation.components.Common.BaseHeader
import org.videotrade.shopot.presentation.components.Common.TextSection

class  DataProcessingAgreement : Screen {


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

                    BaseHeader("Об обработке данных", colors.surface)

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {

                        TextSection(
                            title = "Согласие на обработку персональных данных", content = listOf(
                                "Предоставляя свои персональные данные, Пользователь даёт согласие на обработку, хранение и использование своих персональных данных на основании ФЗ № 152-ФЗ «О персональных данных» от 27.07.2006 г. в следующих целях:",
                                "— Регистрации Пользователя в Приложении",
                                "— Осуществление клиентской поддержки",
                                "— Выполнение Компанией обязательств перед Пользователем",
                                "— Проведения аудита и прочих внутренних исследований с целью повышения качества предоставляемых услуг."
                            )
                        )

                        // Персональные данные
                        TextSection(
                            title = "Персональные данные", content = listOf(
                                "Под персональными данными подразумевается любая информация личного характера, позволяющая установить личность Пользователя такая как:",
                                "— Фамилия, Имя, Отчество",
                                "— Контактный телефон"
                            )
                        )

                        // Хранение и обработка данных
                        TextSection(
                            title = "Хранение и обработка данных", content = listOf(
                                "Персональные данные пользователей хранятся исключительно на электронных носителях и обрабатываются с использованием автоматизированных систем, за исключением случаев, когда неавтоматизированная обработка персональных данных необходима в связи с исполнением требований законодательства."
                            )
                        )

                        // Передача данных
                        TextSection(
                            title = "Передача данных", content = listOf(
                                "Компания обязуется не передавать полученные персональные данные третьим лицам, за исключением следующих случаев:",
                                "— По запросам уполномоченных органов государственной власти РФ только по основаниям и в порядке, установленным законодательством РФ"
                            )
                        )

                        // Изменение правил
                        TextSection(
                            title = "Изменение правил", content = listOf(
                                "Компания оставляет за собой право вносить изменения в одностороннем порядке в настоящие правила, при условии, что изменения не противоречат действующему законодательству РФ. Изменения условий настоящих правил вступают в силу после их публикации на Сайте."
                            )
                        )

                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(10.dp).padding(bottom = 40.dp)
                ) {
                    BaseHeader("Data Processing Notice", colors.surface)

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {
                        TextSection(
                            title = "Consent to the Processing of Personal Data",
                            content = listOf(
                                "By providing their personal data, the User gives consent to the processing, storage, and use of their personal data based on Federal Law No. 152-FZ 'On Personal Data' dated 27.07.2006 for the following purposes:",
                                "— User registration in the Application",
                                "— Providing customer support",
                                "— Fulfilling the Company's obligations to the User",
                                "— Conducting audits and other internal research to improve the quality of services provided."
                            )
                        )

                        TextSection(
                            title = "Personal Data",
                            content = listOf(
                                "Personal data refers to any personal information that allows identifying the User, such as:",
                                "— Last name, First name, Middle name",
                                "— Contact phone number"
                            )
                        )

                        TextSection(
                            title = "Storage and Processing of Data",
                            content = listOf(
                                "User's personal data is stored exclusively on electronic media and processed using automated systems, except in cases where non-automated processing of personal data is required in connection with compliance with legal requirements."
                            )
                        )

                        TextSection(
                            title = "Data Transfer",
                            content = listOf(
                                "The Company undertakes not to transfer the received personal data to third parties, except in the following cases:",
                                "— At the request of authorized government bodies of the Russian Federation, only on the grounds and in the manner established by the legislation of the Russian Federation."
                            )
                        )

                        TextSection(
                            title = "Changes to the Rules",
                            content = listOf(
                                "The Company reserves the right to make changes to these rules unilaterally, provided that such changes do not contradict the current legislation of the Russian Federation. Changes to these rules take effect after they are published on the website."
                            )
                        )
                    }
                }

            }
        }
    }
}