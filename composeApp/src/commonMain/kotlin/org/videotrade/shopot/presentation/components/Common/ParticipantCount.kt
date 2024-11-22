package org.videotrade.shopot.presentation.components.Common

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import org.videotrade.shopot.MokoRes
import kotlin.math.abs

@Composable
fun getParticipantCountText(count: Int): String {
    val forms = arrayOf(
        stringResource(MokoRes.strings.participant),
        stringResource(MokoRes.strings.participants_1),
        stringResource(MokoRes.strings.participants_2)
    )
    return "$count ${getPluralForm(count, forms)}"
}

fun getPluralForm(number: Int, forms: Array<String>): String {
    val n = abs(number) % 100
    val n1 = n % 10
    return when {
        n in 11..19 -> forms[2]
        n1 == 1 -> forms[0]
        n1 in 2..4 -> forms[1]
        else -> forms[2]
    }
}