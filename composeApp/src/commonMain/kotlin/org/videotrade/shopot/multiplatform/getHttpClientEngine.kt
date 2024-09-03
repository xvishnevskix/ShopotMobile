package org.videotrade.shopot.multiplatform

import androidx.compose.ui.Modifier
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory

expect fun getHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig>


expect fun Modifier.hideKeyboardOnTap(): Modifier
