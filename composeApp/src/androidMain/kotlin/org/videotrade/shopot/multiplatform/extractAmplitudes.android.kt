package org.videotrade.shopot.multiplatform

import android.media.MediaExtractor
import android.media.MediaFormat
import java.nio.ByteBuffer

actual fun extractAmplitudes(filePath: String): List<Float> {
    val amplitudes = mutableListOf<Float>()
    val extractor = MediaExtractor()
    extractor.setDataSource(filePath)
    val format = extractor.getTrackFormat(0)
    val mime = format.getString(MediaFormat.KEY_MIME)
    
    if (mime?.startsWith("audio/") == true) {
        extractor.selectTrack(0)
        val buffer = ByteBuffer.allocate(1024)
        while (extractor.readSampleData(buffer, 0) >= 0) {
            val maxAmplitude = buffer.array().maxOrNull()?.toFloat() ?: 0f
            amplitudes.add(maxAmplitude)
            buffer.clear()
            extractor.advance()
        }
    }
    extractor.release()
    
    // Фильтрация низкоуровневого шума и нормализация
    val filteredAmplitudes = amplitudes.map { amplitude -> if (amplitude < 10f) 0f else amplitude }
    val maxAmplitude = filteredAmplitudes.maxOrNull() ?: 1f
    return filteredAmplitudes.map { it / maxAmplitude }
}
