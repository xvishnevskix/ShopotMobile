package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import com.shepeliev.webrtckmp.AudioStreamTrack
import com.shepeliev.webrtckmp.VideoStreamTrack

expect fun getAndSaveFirstFrame(videoFilePath: String, completion: (String?, String?, ByteArray?) -> Unit)


@Composable
expect fun VideoPlayer(modifier: Modifier, filePath: String)


@Composable
expect fun Video(videoTrack: VideoStreamTrack, modifier: Modifier = Modifier, audioTrack: AudioStreamTrack? = null)
