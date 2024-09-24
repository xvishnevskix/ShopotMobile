package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Bitmap
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.valueWithCMTime
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSData
import platform.Foundation.NSMakeRange
import platform.Foundation.NSURL
import platform.Foundation.NSValue
import platform.Foundation.getBytes
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation


@OptIn(ExperimentalForeignApi::class)
actual fun getImageFromVideo(url: String, completion: (ByteArray?) -> Unit) {
    val asset = AVAsset.assetWithURL(NSURL.fileURLWithPath(url))
    val imageGenerator = AVAssetImageGenerator(asset)
    imageGenerator.appliesPreferredTrackTransform = true
    val time = CMTimeMake(0, 600) // Первый кадр
    
    imageGenerator.generateCGImagesAsynchronouslyForTimes(
        listOf(NSValue.valueWithCMTime(time))
    ) { _, cgImagePointer, _, result, error ->
        if (error != null) {
            println("Ошибка извлечения изображения: ${error.localizedDescription}")
            completion(null)
            return@generateCGImagesAsynchronouslyForTimes
        }
        
        if (cgImagePointer != null) {
            val uiImage = UIImage.imageWithCGImage(cgImagePointer)
            val jpegData = UIImageJPEGRepresentation(uiImage, 1.0) // Получение JPEG данных
            
            jpegData?.let {
                completion(jpegData.toByteArray()) // Возврат ImageBitmap
            } ?: completion(null) // Если jpegData пустое
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

actual fun getFirstFrameAsBitmap(videoFilePath: String): Bitmap? {
    TODO("Not yet implemented")
}

actual fun saveBitmapToFile(
    bitmap: ImageBitmap,
    fileName: String
): ByteArray? {
    TODO("Not yet implemented")
}