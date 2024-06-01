package org.videotrade.shopot.multiplatform

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream
import kotlin.coroutines.resume

actual class MediaProvider(private val activity: ComponentActivity) {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var currentContinuation: CancellableContinuation<String>? = null
    
    fun initialize() {
        resultLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                currentContinuation?.resume(uri.toString())
            } else {
                currentContinuation?.resume("")
            }
        }
    }
    
    actual suspend fun getMedia(): String = suspendCancellableCoroutine { continuation ->
        currentContinuation = continuation
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/* video/*"
        }
        resultLauncher.launch(intent)
    }
}

@SuppressLint("StaticFieldLeak")
actual object MediaProviderFactory {
    private lateinit var activity: ComponentActivity
    private lateinit var mediaProvider: MediaProvider
    
    @SuppressLint("StaticFieldLeak")
    fun initialize(activity: ComponentActivity) {
        this.activity = activity
        mediaProvider = MediaProvider(activity)
        mediaProvider.initialize()
    }
    
    @SuppressLint("StaticFieldLeak")
    actual fun create(): MediaProvider {
        return mediaProvider
    }
}

@Composable
actual fun loadImage(uri: String): ImageBitmap? {
    val context = LocalContext.current
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(Uri.parse(uri))
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}