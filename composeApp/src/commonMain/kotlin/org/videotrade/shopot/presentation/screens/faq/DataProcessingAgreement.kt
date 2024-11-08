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
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import org.videotrade.shopot.presentation.components.Common.CustomButton
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold

class  DataProcessingAgreement () : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow


        Box(
            modifier = Modifier
                .fillMaxSize().background(Color(0xFFf9f9f9))
        ) {
            Column(
                modifier = Modifier.padding(10.dp).padding(bottom = 40.dp)
            ) {

                BaseHeader("Об обработке данных", Color(0xFFf9f9f9))

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
        }
    }

    @Composable
    fun TextSection(title: String, content: List<String>) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular))
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        content.forEach {
            Text(
                text = it,
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color(0xFF29303C),
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 16.dp)
            )
        }
    }
}