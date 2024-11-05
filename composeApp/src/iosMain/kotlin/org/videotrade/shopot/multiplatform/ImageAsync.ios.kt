package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.core.PickerType
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.readBytes
import io.ktor.util.InternalAPI
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.api.getValueInStorage
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGBitmapInfo
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import org.jetbrains.skia.*
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ImageInfo
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.get
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.*
import platform.Foundation.*

actual suspend fun imageAsync(imageId: String, imageName: String, isCipher: Boolean): ImageBitmap? {
    val imageExist = FileProviderFactory.create().existingFileInDir(imageId, "image")
    val filePath = imageExist ?: downloadImageInCache(imageId)
    
    return  if (filePath != null) {
        withContext(Dispatchers.IO) {
            val fileUrl = NSURL.fileURLWithPath(filePath)
            val imageData = NSData.dataWithContentsOfURL(fileUrl)
            
            val uiImage = imageData?.let { UIImage.imageWithData(it) }
            
            uiImage?.toImageBitmap()
        }
    } else {
        null
    }
}


@OptIn(InternalAPI::class, ExperimentalForeignApi::class)
private suspend fun downloadImageInCache(imageId: String): String? {
    val client = HttpClient(getHttpClientEngine())
    val filePath =
        FileProviderFactory.create().createNewFileWithApp(imageId, "image") ?: return null
    
    println("starting download")
    
    try {
        val token = getValueInStorage("accessToken")
            ?: throw IllegalStateException("Access token is missing")
        
        // Подготовка запроса для скачивания файла
        client.prepareGet("${serverUrl}file/plain/$imageId") {
            header("Authorization", "Bearer $token")
        }.execute { httpResponse ->
            val fileManager = NSFileManager.defaultManager
            val fileUrl = NSURL.fileURLWithPath(filePath)
            
            // Убедимся, что файл существует; если нет — создаем пустой файл
            if (!fileManager.fileExistsAtPath(filePath)) {
                fileManager.createFileAtPath(filePath, null, null)
            }
            
            try {
                // Получение данных из ответа и запись в файл
                val data = httpResponse.readBytes()
                val nsData = data.usePinned { pinned ->
                    NSData.dataWithBytes(pinned.addressOf(0), data.size.toULong())
                }
                
                if (nsData.writeToURL(fileUrl, true)) {
                    println("Image successfully downloaded and saved to: $filePath")
                } else {
                    println("Error writing file to path: $filePath")
                    return@execute null
                }
            } catch (e: Exception) {
                println("Error writing file: ${e.message}")
                e.printStackTrace()
                return@execute null
            }
        }
        
        return filePath
        
    } catch (e: Exception) {
        println("Error downloading file: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
    return null
}



@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toSkiaImage(): Image? {
    val imageRef = this.CGImage ?: return null
    
    val width = CGImageGetWidth(imageRef).toInt()
    val height = CGImageGetHeight(imageRef).toInt()
    
    val bytesPerRow = CGImageGetBytesPerRow(imageRef)
    val data = CGDataProviderCopyData(CGImageGetDataProvider(imageRef))
    val bytePointer = CFDataGetBytePtr(data)
    val length = CFDataGetLength(data)
    
    val alphaType = when (CGImageGetAlphaInfo(imageRef)) {
        CGImageAlphaInfo.kCGImageAlphaPremultipliedFirst,
        CGImageAlphaInfo.kCGImageAlphaPremultipliedLast -> ColorAlphaType.PREMUL
        CGImageAlphaInfo.kCGImageAlphaFirst,
        CGImageAlphaInfo.kCGImageAlphaLast -> ColorAlphaType.UNPREMUL
        CGImageAlphaInfo.kCGImageAlphaNone,
        CGImageAlphaInfo.kCGImageAlphaNoneSkipFirst,
        CGImageAlphaInfo.kCGImageAlphaNoneSkipLast -> ColorAlphaType.OPAQUE
        else -> ColorAlphaType.UNKNOWN
    }
    
    val byteArray = ByteArray(length.toInt()) { index ->
        bytePointer!![index].toByte()
    }
    
    CFRelease(data)
    CGImageRelease(imageRef)
    
    val skiaColorSpace = ColorSpace.sRGB
    val colorType = ColorType.RGBA_8888
    
    // Convert RGBA to BGRA
    for (i in byteArray.indices step 4) {
        val r = byteArray[i]
        val g = byteArray[i + 1]
        val b = byteArray[i + 2]
        val a = byteArray[i + 3]
        
        byteArray[i] = b
        byteArray[i + 2] = r
    }
    
    return Image.makeRaster(
        imageInfo = ImageInfo(
            width = width,
            height = height,
            colorType = colorType,
            alphaType = alphaType,
            colorSpace = skiaColorSpace
        ),
        bytes = byteArray,
        rowBytes = bytesPerRow.toInt(),
    )
}

fun UIImage.toImageBitmap(): ImageBitmap {
    val skiaImage = this.toSkiaImage() ?: return ImageBitmap(1, 1)
    return skiaImage.toComposeImageBitmap()
}