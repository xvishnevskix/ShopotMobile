package org.videotrade.shopot.multiplatform

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.http.ContentType




@Composable
fun LanguageSelector() {
    Button(onClick = {
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")

    }) {
        Text(text = "English")
    }

    Button(onClick = {
        StringDesc.localeType = StringDesc.LocaleType.Custom("ru")

    }) {
        Text(text = "Русский")
    }
}