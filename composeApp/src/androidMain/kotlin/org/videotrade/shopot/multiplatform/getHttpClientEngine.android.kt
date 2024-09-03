package org.videotrade.shopot.multiplatform

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import org.videotrade.shopot.androidSpecificApi.getContextObj
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

actual fun getHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig> {
    return OkHttp
}

actual fun Modifier.hideKeyboardOnTap(): Modifier {
    return this.then(Modifier)
}