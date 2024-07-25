package org.videotrade.shopot.api

fun formatSize(sizeInBytes: Long): String {
    val sizeInKb = sizeInBytes / 1024.0
    val sizeInMb = sizeInKb / 1024.0
    val sizeInGb = sizeInMb / 1024.0
    
    return when {
        sizeInGb >= 1 -> "${(sizeInGb * 100).toInt() / 100.0} GB"
        sizeInMb >= 1 -> "${(sizeInMb * 100).toInt() / 100.0} MB"
        sizeInKb >= 1 -> "${(sizeInKb * 100).toInt() / 100.0} kB"
        else -> "$sizeInBytes bytes"
    }
}