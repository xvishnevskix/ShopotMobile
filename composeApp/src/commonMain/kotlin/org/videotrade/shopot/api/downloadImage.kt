package org.videotrade.shopot.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.util.decodeBase64Bytes
import io.ktor.utils.io.ByteReadChannel
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import kotlin.math.roundToInt

