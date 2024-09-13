
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res

@Composable
fun PhotoViewerHeader(name: String, time: String) {
    val navigator = LocalNavigator.currentOrThrow

    Row(
        modifier = Modifier
            .background(Color(0xFF29303C).copy(alpha = 0.8f))
            .fillMaxWidth()
            .padding(top = 30.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                   navigator.pop()
                }
                .width(20.dp),
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(
                text = if (name == "") "Вы" else name,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                fontSize = 16.sp,
                color = Color.White
            )
//            Text(
//                text = "",
//                fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
//                fontSize = 14.sp,
//                color = Color.White
//            )
        }
    }
}
