package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path

expect fun getAndSaveFirstFrame(videoFilePath: String, completion: (String?, String?, ByteArray?) -> Unit)


@Composable
expect fun VideoPlayer(modifier: Modifier, filePath: String)
