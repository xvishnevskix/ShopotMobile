package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.readBytes
import io.ktor.util.InternalAPI
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.get
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.api.getValueInStorage
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.CGDataProviderCopyData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageGetAlphaInfo
import platform.CoreGraphics.CGImageGetBytesPerRow
import platform.CoreGraphics.CGImageGetDataProvider
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGImageRelease
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import platform.ImageIO.*
import platform.CoreGraphics.CGAffineTransformMakeRotation
import platform.CoreGraphics.CGFloat
import platform.CoreGraphics.CGSizeMake
import platform.CoreGraphics.CGImageCreateWithImageInRect
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGImageRef
import kotlinx.cinterop.memScoped
import kotlin.math.PI

actual suspend fun imageAsync(imageId: String, imageName: String, isCipher: Boolean): ImageBitmap? {
    val imageExist = FileProviderFactory.create().existingFileInDir(imageId, "image")
    val filePath = imageExist ?: downloadImageInCache(imageId)
    
    return if (filePath != null) {
        withContext(Dispatchers.IO) {
            val fileUrl = NSURL.fileURLWithPath(filePath)
            val imageData = NSData.dataWithContentsOfURL(fileUrl)
            
            val uiImage = imageData?.let { UIImage.imageWithData(it) }
            
            withContext(Dispatchers.Main) {
                uiImage?.toImageBitmap()
            }
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
    
    if (bytePointer == null) {
        CFRelease(data)
        CGImageRelease(imageRef)
        return null
    }
    
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
        bytePointer[index].toByte()
    }
    
    CFRelease(data)
    CGImageRelease(imageRef)
    
    val skiaColorSpace = ColorSpace.sRGB
    val colorType = ColorType.BGRA_8888 // Changed to BGRA to preserve original color
    
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

actual suspend fun imageAsyncIos(
    imageId: String,
    imageName: String,
    isCipher: Boolean
): ByteArray? {
    val imageExist = FileProviderFactory.create().existingFileInDir(imageId, "image")
    val filePath = imageExist ?: downloadImageInCache(imageId)
    
    return if (filePath != null) {
        withContext(Dispatchers.IO) {
            val fileUrl = NSURL.fileURLWithPath(filePath)
            val imageData = NSData.dataWithContentsOfURL(fileUrl)
            
            imageData?.toByteArray()
        }
    } else {
        null
    }
}



