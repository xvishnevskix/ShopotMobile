package org.videotrade.shopot.multiplatform

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.shepeliev.webrtckmp.AudioStreamTrack
import com.shepeliev.webrtckmp.VideoStreamTrack
import com.shepeliev.webrtckmp.WebRtc
import org.videotrade.shopot.androidSpecificApi.getContextObj
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoSink
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random


actual fun getAndSaveFirstFrame(
    videoFilePath: String,
    completion: (String?, String?, ByteArray?) -> Unit
) {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(videoFilePath)
        val bitmap = retriever.getFrameAtTime(0) // Получаем первый кадр (на временной отметке 0)
        val fileName = "${Random.nextInt(1, 2001)}_frame.png"
        bitmap?.let {
            val downloadsDir = File(
                getContextObj.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "Images"
            )
            
            
            val file = File(downloadsDir, fileName)
            
            // Сохраняем Bitmap в файл
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            // Получаем массив байтов из Bitmap
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, stream)
            
            completion(
                fileName,
                file.absolutePath,
                stream.toByteArray()
            ) // Возвращаем путь к файлу и массив байтов
        } ?: completion(null, null, null)
    } catch (e: Exception) {
        e.printStackTrace()
        completion(null, null, null)
    } finally {
        retriever.release()
    }
}

@Composable
actual fun VideoPlayer(modifier: Modifier, filePath: String) {
    // Преобразование пути к файлу в URI
    if (!File(filePath).exists()) {
        return
    }
    
    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).apply {
                setVideoPath(filePath) // Установка URI вместо прямого пути
                val mediaController = MediaController(context)
                mediaController.setAnchorView(this)
                setMediaController(mediaController)
                start()
            }
        },
        update = {}
    )
}

@Composable
actual fun Video(videoTrack: VideoStreamTrack, modifier: Modifier, audioTrack: AudioStreamTrack?) {
    var renderer by remember { mutableStateOf<SurfaceViewRenderer?>(null) }
    
    val lifecycleEventObserver = remember(renderer, videoTrack) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    renderer?.also {
                        it.init(WebRtc.rootEglBase.eglBaseContext, null)
                        videoTrack.addSinkCatching(it)
                    }
                }
                
                Lifecycle.Event.ON_PAUSE -> {
                    renderer?.also { videoTrack.removeSinkCatching(it) }
                    renderer?.release()
                }
                
                else -> {
                    // ignore other events
                }
            }
        }
    }
    
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, lifecycleEventObserver) {
        lifecycle.addObserver(lifecycleEventObserver)
        
        onDispose {
            renderer?.let { videoTrack.removeSinkCatching(it) }
            renderer?.release()
            lifecycle.removeObserver(lifecycleEventObserver)
        }
    }
    
    AndroidView(
        modifier = modifier,
        factory = { context ->
            SurfaceViewRenderer(context).apply {
                setScalingType(
                    RendererCommon.ScalingType.SCALE_ASPECT_BALANCED,
                    RendererCommon.ScalingType.SCALE_ASPECT_FIT
                )
                renderer = this
            }
        },
    )
}

private fun VideoStreamTrack.addSinkCatching(sink: VideoSink) {
    // runCatching as track may be disposed while activity was in pause
    runCatching { addSink(sink) }
}

private fun VideoStreamTrack.removeSinkCatching(sink: VideoSink) {
    // runCatching as track may be disposed while activity was in pause
    runCatching { removeSink(sink) }
}
