package org.videotrade.shopot.multiplatform

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.http.ContentType
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.api.getValueInStorage


@Composable
fun LanguageSelector()  {



    Button(onClick = {
        delValueInStorage("selected_language")
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")
        addValueInStorage("selected_language", "en")
    }) {
        Text(text = "English")
    }

    Button(onClick = {
        delValueInStorage("selected_language")
        StringDesc.localeType = StringDesc.LocaleType.Custom("ru")
        addValueInStorage("selected_language", "ru")

    }) {
        Text(text = "Русский")
    }
}
@Composable
fun AppInitializer() {
    val storedLanguage = getValueInStorage("selected_language")
    StringDesc.localeType = when (storedLanguage) {
        "en" -> StringDesc.LocaleType.Custom("en")
        "ru" -> StringDesc.LocaleType.Custom("ru")
        else -> StringDesc.LocaleType.System
    }
}
