package org.videotrade.shopot.multiplatform

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory

expect fun getHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig>
