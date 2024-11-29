package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.Font
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res

@Composable
fun ModalDialogWithoutText(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (CoroutineScope) -> Unit = {},
    confirmText: String = "Подтвердить",
    dismissText: String = "Отмена"
) {
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(colors.background)
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        color = colors.primary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        CustomButton(
                            text = confirmText,
                            onClick = {
                                onConfirm(scope)
                            },
                            style = ButtonStyle.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomButton(
                            text = dismissText,
                            onClick = { onDismiss() },
                            style = ButtonStyle.Gradient
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModalDialogWithText(
    text: String,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (CoroutineScope) -> Unit = {},
    confirmText: String,
    dismissText: String = "Отмена"
) {
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
            ) {
                Column(
                    modifier = Modifier
                        .background(colors.background)
                        .padding(30.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        color = colors.primary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                        fontWeight = FontWeight(400),
                        color = colors.secondary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomButton(
                            text = confirmText,
                            onClick = {
                                onConfirm(scope)
                            },
                            style = ButtonStyle.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomButton(
                            text = dismissText,
                            onClick = { onDismiss() },
                            style = ButtonStyle.Gradient
                        )
                    }
                }
            }
        }
    }
}
