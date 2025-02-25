package org.videotrade.shopot.multiplatform

import WebRTC.RTCMTLVideoView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.valueWithCMTime
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSMakeRange
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSValue
import platform.Foundation.getBytes
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import kotlin.random.Random
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.UIKitView
import com.shepeliev.webrtckmp.AudioStreamTrack
import com.shepeliev.webrtckmp.VideoStreamTrack
import io.ktor.client.engine.darwin.*
import kotlinx.cinterop.CValue
import org.koin.dsl.module
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.play
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView
import platform.UIKit.UIViewContentMode


@OptIn(ExperimentalForeignApi::class)
actual fun getAndSaveFirstFrame(
    videoFilePath: String,
    completion: (String?, String?, ByteArray?) -> Unit
) {
    val asset = AVAsset.assetWithURL(NSURL.fileURLWithPath(videoFilePath))
    val imageGenerator = AVAssetImageGenerator(asset)
    imageGenerator.appliesPreferredTrackTransform = true
    val time = CMTimeMake(0, 600) // Первый кадр
    
    imageGenerator.generateCGImagesAsynchronouslyForTimes(
        listOf(NSValue.valueWithCMTime(time))
    ) { _, cgImagePointer, _, result, error ->
        if (error != null) {
            println("Ошибка извлечения изображения: ${error.localizedDescription}")
            completion(null, null, null)
            return@generateCGImagesAsynchronouslyForTimes
        }
        
        if (cgImagePointer != null) {
            val uiImage = UIImage.imageWithCGImage(cgImagePointer)
            val jpegData = UIImageJPEGRepresentation(uiImage, 1.0) // Получение JPEG данных
            
            jpegData?.let {
                // Указываем папку "Документы"
                val documentsDir = NSSearchPathForDirectoriesInDomains(
                    NSDocumentDirectory, NSUserDomainMask, true
                ).firstOrNull() as? String ?: run {
                    completion(null, null, null)
                    return@let
                }
                
                // Генерация уникального имени файла
                val fileName = "${Random.nextInt(1, 2001)}_frame.jpg"
                val filePath = documentsDir + "/" + fileName
                val fileUrl = NSURL.fileURLWithPath(filePath)
                
                // Сохранение файла
                if (it.writeToURL(fileUrl, true)) {
                    // Преобразование данных в ByteArray
                    val byteArray = it.toByteArray()
                    completion(fileName, filePath, byteArray) // Возврат пути к файлу и ByteArray
                } else {
                    completion(null, null, null) // Если файл не удалось сохранить
                }
            } ?: completion(null, null, null) // Если jpegData пустое
        } else {
            completion(null, null, null) // Если cgImagePointer пустое
        }
    }
}

// Helper function to convert NSData to ByteArray
@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    val byteArray = ByteArray(length)
    byteArray.usePinned {
        this.getBytes(it.addressOf(0), NSMakeRange(0.toULong(), length.toULong()))
    }
    return byteArray
}






@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(modifier: Modifier, filePath: String) {
    // Преобразуем абсолютный путь в URL для iOS
    val nsUrl = NSURL.fileURLWithPath(filePath) // Используем fileURLWithPath для локальных файлов
    
    val player = remember { AVPlayer(uRL = nsUrl) }
    val playerLayer = remember { AVPlayerLayer() }
    val avPlayerViewController = remember { AVPlayerViewController() }
    avPlayerViewController.player = player
    avPlayerViewController.showsPlaybackControls = true
    
    playerLayer.player = player
    
    // Используем UIKitView для интеграции с существующими представлениями UIKit
    UIKitView(
        factory = {
            // Создаем UIView для удержания AVPlayerLayer
            val playerContainer = UIView()
            playerContainer.addSubview(avPlayerViewController.view)
            // Возвращаем playerContainer как корневой UIView
            playerContainer
        },
        onResize = { view: UIView, rect: CValue<CGRect> ->
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)
            view.layer.setFrame(rect)
            playerLayer.setFrame(rect)
            avPlayerViewController.view.layer.frame = rect
            CATransaction.commit()
        },
        update = { view ->
            player.play()
            avPlayerViewController.player!!.play()
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Video(videoTrack: VideoStreamTrack, modifier: Modifier, audioTrack: AudioStreamTrack?) {
    UIKitView(
        factory = {
            RTCMTLVideoView().apply {
                videoContentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
                videoTrack.addRenderer(this)
            }
        },
        modifier = modifier,
        onRelease = { videoTrack.removeRenderer(it) }
    )
}
