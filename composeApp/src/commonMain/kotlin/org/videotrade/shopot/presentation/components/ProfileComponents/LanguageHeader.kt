import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.presentation.tabs.ChatsTab
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Res

@Composable
fun LanguageHeader(text: String) {
    val navigator = LocalNavigator.currentOrThrow
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.padding(start = 10.dp,end = 1.dp).pointerInput(Unit) {
                    navigator.pop()
                },
                tint = Color.Black
            )

        Text(
            text = text,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Center,
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 24.sp,
            color = Color.Black

        )

        Spacer(modifier = Modifier)
    }

}
