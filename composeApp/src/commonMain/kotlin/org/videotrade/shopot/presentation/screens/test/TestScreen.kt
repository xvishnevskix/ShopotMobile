//package org.videotrade.shopot.presentation.screens.test
//
////import org.videotrade.shopot.multiplatform.encupsChachaMessage
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import cafe.adriel.voyager.core.screen.Screen
//import io.ktor.utils.io.charsets.Charsets
//import io.ktor.utils.io.core.toByteArray
//import org.videotrade.shopot.multiplatform.decupsChachaMessage
//import org.videotrade.shopot.multiplatform.encupsChachaMessage
//import org.videotrade.shopot.multiplatform.sharedSecret
//
//class TestScreen : Screen {
//    @Composable
//    override fun Content() {
//        var publicKey by remember { mutableStateOf("opsda") }
//
//        var errorMessage by remember { mutableStateOf("") }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Button(onClick = {
//                try {
//                    val publicKeyBytes = publicKey.toByteArray()
//
//                    val result = sharedSecret(publicKeyBytes)
//
//                    val result2 = encupsChachaMessage("privet", result[1])
//
//                    val result3 = decupsChachaMessage(
//                        cipher = result2.cipher,
//                        block = result2.block,
//                        authTag = result2.authTag,
//                        result[1]
//                    )
//
//                    println("result3 $result3")
//
//                } catch (e: Exception) {
//                    errorMessage = "Error: ${e.message}"
//                }
//            }) {
//                Text("Generate Shared Secret")
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//        }
//    }
//}
//
//


package org.videotrade.shopot.presentation.screens.test

//import org.videotrade.shopot.multiplatform.encupsChachaMessage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.ByteString.Companion.decodeBase64
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.FileProviderFactory
import kotlin.random.Random

class TestScreen : Screen {
    @Composable
    override fun Content() {
        val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
        
        var publicKey by remember { mutableStateOf("opsda") }
        
        var errorMessage by remember { mutableStateOf("") }
        var showFilePicker by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        
        
        val filterFileType = listOf("pdf", "zip")
        FilePicker(show = showFilePicker, fileExtensions = filterFileType) { platformFile ->
            showFilePicker = false
            // do something with the file
            
            println("showFilePicker ${platformFile?.platformFile} ${platformFile?.path}")
            
            if (platformFile?.path !== null) {
                
                scope.launch {
                    try {
                        println("11111 ")
                        
                        
                        val publicKeyBytes = publicKey.toByteArray()
                        
                        val result = cipherWrapper.getSharedSecretCommon(publicKeyBytes)
                        
                        val cipherFilePath = FileProviderFactory.create()
                            .getFilePath(
                                "cipherFile${Random.nextInt(0, 100000)}",
                                "pdf"
                            )
                        
                        println("asdadadasd ${platformFile.path}")
                        val result2 =
                            cipherWrapper.encupsChachaFileCommon(
                                platformFile.path,
                                cipherFilePath,
                                result?.sharedSecret!!
                            )
                        
                        println("result2 $result2")
                        
                        val decupsFile = FileProviderFactory.create()
                            .getFilePath(
                                "decupsFile${Random.nextInt(0, 100000)}.pdf",
                                "pdf"
                            )
                        
                        println("dadadada $cipherFilePath $decupsFile ${result2?.block!!} ${result2.authTag} ${result.sharedSecret}")
                        
                        val result3 =
                            cipherWrapper.decupsChachaFileCommon(
                                cipherFilePath,
                                decupsFile,
                                result2?.block!!,
                                result2.authTag,
                                result.sharedSecret
                            )
                        
                        
                        println("result3 $result3")
                        
                        
                    } catch (e: Exception) {
                        
                        println("error $e")
                        
                    }
                    
                    
                }
                
                
            }
        }
        
        
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(onClick = {
                try {
                    showFilePicker = true
                    
                    
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                }
            }) {
                Text("Generate Shared Secret")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
        }
    }
}


