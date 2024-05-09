package org.videotrade.shopot.presentation.components.Call

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shepeliev.webrtckmp.VideoStreamTrack

@Composable
expect fun Video(track: VideoStreamTrack, modifier: Modifier = Modifier)
expect fun Video2(track: VideoStreamTrack, modifier: Modifier = Modifier)
