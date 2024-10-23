import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res

@Composable
fun ViewerHeader(name: String, time: String = "") {
    val navigator = LocalNavigator.currentOrThrow
    
    Row(
        modifier = Modifier
            .background(Color(0xFF29303C).copy(alpha = 0.8f))
            .fillMaxWidth()
            .statusBarsPadding()
//            .padding(top = 30.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
            .padding(top = 5.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                text = if (name == "") stringResource(MokoRes.strings.you) else name,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                fontSize = 16.sp,
                color = Color.White
            )
            
            if (time.isNotEmpty())
                Text(
                    text = time,
                    fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
                    fontSize = 14.sp,
                    color = Color.White
                )
        }
    }
}
