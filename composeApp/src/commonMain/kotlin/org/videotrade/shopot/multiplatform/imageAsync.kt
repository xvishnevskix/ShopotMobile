package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun imageAsync(imageId: String): ByteArray?
