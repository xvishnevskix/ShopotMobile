package org.videotrade.shopot.multiplatform

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin


actual fun getHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig> {
    return Darwin
}