package org.videotrade.shopot.presentation.components.Common

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res

data class PopupOption(
    val text: String,
    val onClick: () -> Unit,
    val imagePath: DrawableResource,
    val modifier: Modifier = Modifier,
    val color: Color = Color(0xFF373533),
)
enum class GroupEditOption() {
    edit,
    remove;

    fun toPopupDTO(text: String, onClick: () -> Unit, imagePath: DrawableResource, modifier: Modifier, color: Color): PopupOption = when (this) {
        edit -> PopupOption(text,onClick,imagePath, modifier, color)
        remove -> PopupOption(text, onClick, imagePath, modifier, color)
    }
}

@Composable
fun EditOptionsPopup(
    isVisible: Boolean,
    popupDTO: List<PopupOption>,
    onDismissRequest: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    if (isVisible) {
        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(20, 200), // настроить под якорь
            onDismissRequest = onDismissRequest
        ) {
            Crossfade(targetState = isVisible, label = "EditPopup") { visible ->
                if (visible) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.background)
                            .border(
                                width = 1.dp,
                                color = colors.onSecondary,
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .shadow(1.dp)
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    ) {
                        popupDTO.forEach { item ->
                            Column {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = colors.onSecondary,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .width(197.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            item.onClick()
                                            onDismissRequest()
                                        }
                                        .padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 16.dp,
                                            bottom = 16.dp
                                        )
                                ) {
                                    Text(
                                        text = item.text,
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        color = colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    )
                                    Image(
                                        painter = painterResource(item.imagePath),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        colorFilter = ColorFilter.tint(item.color)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}