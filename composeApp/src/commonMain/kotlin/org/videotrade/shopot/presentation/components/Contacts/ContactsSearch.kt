package org.videotrade.shopot.presentation.components.Contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Common.CustomTextField
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Res

@Composable
fun ContactsSearch(
    searchQuery: MutableState<String>,
    isSearching: MutableState<Boolean>,
    padding: Dp = 16.dp
    ) {

    Column(
        modifier = Modifier.padding(horizontal = padding).fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth(0.75f)) {
                CustomTextField(
                    value = searchQuery.value,
                    onValueChange = { newText -> searchQuery.value = newText },
                    placeholder = stringResource(MokoRes.strings.search),
                )
            }
            Box(
                modifier = Modifier.padding(horizontal = 5.dp).width(68.dp).pointerInput(Unit) {
                    isSearching.value = false
                    searchQuery.value = ""
                },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(MokoRes.strings.cancel),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFF373533),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                )
            }
        }

    }

}