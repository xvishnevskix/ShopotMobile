package videotrade.parkingProj.presentation.components.Common.Common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun PaymentMethodCard(
    title: String,
    subtitle: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    icon: DrawableResource,
    iconSize: Pair<Int, Int> = 30 to 20,
    modifier: Modifier = Modifier,
) {
    val cardBorderModifier = if (isSelected) {
        Modifier.border(
            width = 1.dp,
            color = Color(0xFF4CAF50),
            shape = RoundedCornerShape(8.dp)
        )
    } else Modifier

    Card(
        modifier = modifier
//            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .then(cardBorderModifier)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(
                    width = iconSize.first.dp,
                    height = iconSize.second.dp
                ),
                painter = painterResource(icon),
                contentDescription = null,
                contentScale = ContentScale.Crop,
//                colorFilter = ColorFilter.tint(Color(0xFF2E8BB7)) // можешь сделать параметром
            )

            Spacer(Modifier.width(16.dp))

            Column {
                CustomText(
                    text = title,
                    type = TextType.PRIMARY,
                    fontStyle = FontStyleType.Regular
                )
                Spacer(modifier = Modifier.height(4.dp))
                CustomText(
                    text = subtitle,
                    type = TextType.SECONDARY,
                    fontStyle = FontStyleType.Regular
                )
            }
        }
    }
}
