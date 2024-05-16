package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium

@Composable
fun CustomButton(
    text : String,
    onClick: () -> Unit,
    width: Dp = 325.dp, // default width
    height: Dp = 48.dp,

) {
    Button(
        onClick = onClick,
        // Use ButtonDefaults.buttonColors if you need to customize the colors further
        colors = ButtonDefaults.buttonColors(Color(0xFFb2A293C)),
        shape = RoundedCornerShape(24), // This gives us the rounded corners
        modifier = Modifier.padding().width(width) // set the width
            .height(height)
            .shadow(
                elevation = 15.dp, // радиус размытия
                shape = RoundedCornerShape(0.dp), // форма тени, 0 dp для квадратной
                clip = false, // не обрезать контент под тенью
                ambientColor = Color.Black.copy(alpha = 0.25F), // цвет тени
                spotColor = Color.Black.copy(alpha = 0.25F) // усилить тень в направлении
            )
            .background(Color.Transparent),


        ) {
        Text(
            text = text,
            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 15.sp,
            color = Color(255, 255, 255),
        )
    }
}


