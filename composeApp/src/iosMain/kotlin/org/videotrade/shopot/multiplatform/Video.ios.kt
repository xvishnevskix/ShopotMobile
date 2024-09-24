package org.videotrade.shopot.multiplatform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Bitmap
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.valueWithCMTime
import platform.CoreMedia.CMTimeMake
import platform.Foundation.*
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import kotlin.random.Random
import kotlinx.coroutines.*

@OptIn(ExperimentalForeignApi::class)
actual fun getAndSaveFirstFrame(videoFilePath: String, completion: (String?, ByteArray?) -> Unit) {
    val asset = AVAsset.assetWithURL(NSURL.fileURLWithPath(videoFilePath))
    val imageGenerator = AVAssetImageGenerator(asset)
    imageGenerator.appliesPreferredTrackTransform = true
    val time = CMTimeMake(0, 600) // Первый кадр
    
    imageGenerator.generateCGImagesAsynchronouslyForTimes(
        listOf(NSValue.valueWithCMTime(time))
    ) { _, cgImagePointer, _, result, error ->
        if (error != null) {
            println("Ошибка извлечения изображения: ${error.localizedDescription}")
            completion(null, null)
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
                    completion(null, null)
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
                    completion(filePath, byteArray) // Возврат пути к файлу и ByteArray
                } else {
                    completion(null, null) // Если файл не удалось сохранить
                }
            } ?: completion(null, null) // Если jpegData пустое
        } else {
            completion(null, null) // Если cgImagePointer пустое
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
