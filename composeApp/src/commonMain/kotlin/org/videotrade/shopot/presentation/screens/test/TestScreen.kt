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
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.components.Common.SafeArea
import kotlin.random.Random

class TestScreen : Screen {
    @Composable
    override fun Content() {
        val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
        
        var publicKey by remember { mutableStateOf("opsda") }
        var cipherFilePath2 by remember { mutableStateOf("opsda") }
        var fileName2 by remember { mutableStateOf("opsda") }
        
        var errorMessage by remember { mutableStateOf("") }
        var showFilePicker by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        var fileId by remember { mutableStateOf("") }


//        val filterFileType = listOf("pdf", "zip")
//        FilePicker(show = showFilePicker, fileExtensions = filterFileType) { platformFile ->
//            showFilePicker = false
//            // do something with the file
//
//            println("showFilePicker ${platformFile?.platformFile} ${platformFile?.path}")
//
//            if (platformFile?.path !== null) {
//
//                scope.launch {
//                    try {
//                        println("11111 ")
////
////
//                        val publicKeyBytes = publicKey.toByteArray()
//
//                        val result = cipherWrapper.getSharedSecretCommon(publicKeyBytes)
//
//                        val fileName = "cipherFile${Random.nextInt(0, 100000)}"
//
//                        val cipherFilePath = FileProviderFactory.create()
//                            .getFilePath(
//                                fileName,
//                                "cipher1"
//                            )
//
//                        cipherFilePath2 = cipherFilePath
//                        fileName2 = fileName
//
//                        println("platformFile ${platformFile.path}")
//
//
//                        val fileData = FileProviderFactory.create().getFileData(platformFile.path)
//
//                        println("fileData $fileData")


//                        val sendFile = FileProviderFactory.create().uploadCipherFile(
//                            "file/upload",
//                            platformFile.path,
//                            cipherFilePath,
//                            fileData?.fileType!!,
//                            fileName
//                        ) {
//
//                        }
//
//
//                        if (sendFile !== null)
//                            fileId = sendFile

//                        val sharedSecret = getValueInStorage("sharedSecret")
//
//                        println("sharedSecret $sharedSecret ${sharedSecret?.decodeBase64Bytes()?.size}")
//
//                        val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
//
//                        val encupsChachaFileResult = cipherWrapper.encupsChachaFileCommon(
//                            platformFile.path,
//                            cipherFilePath,
//                            sharedSecret?.decodeBase64Bytes()!!
//                        )
//
//
//                        println("result2 $encupsChachaFileResult")
//
//                        val decupsFile = FileProviderFactory.create()
//                            .getFilePath(
//                                "decupsFile${Random.nextInt(0, 100000)}.pdf",
//                                "pdf"
//                            )
//
//
//
//                        println("dadadada ${EncapsulationMessageResult(encupsChachaFileResult?.block!!, encupsChachaFileResult.authTag, sharedSecret?.decodeBase64Bytes()!!)} $cipherFilePath $decupsFile")
//
//                        val result3 =
//                            cipherWrapper.decupsChachaFileCommon(
//                                cipherFilePath,
//                                decupsFile,
//                                encupsChachaFileResult?.block!!,
//                                encupsChachaFileResult.authTag,
//                                sharedSecret?.decodeBase64Bytes()!!
//
//                            )
//
//
//                        println("result3 $result3")


//                    } catch (e: Exception) {
//
//                        println("error $e")
//
//                    }
//
//
//                }
//
//
//            }
//        }


//        LaunchedEffect(Unit) {
//            showFilePicker = true
//
//        }
//
        
        
        SafeArea {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(onClick = {
                    try {
                        scope.launch {
                            val absltPath = FileProviderFactory.create()
                                .pickFileAndGetAbsolutePath(PickerType.File(listOf("pdf", "zip")))
                            
                            
                            if (absltPath !== null) {
                                
                                val fileName = "cipherFile${Random.nextInt(0, 100000)}"
                                
                                
                                val fileData =
                                    FileProviderFactory.create()
                                        .getFileData(absltPath.fileContentPath)
                                
                                
                                if (fileData !== null) {
                                    val cipherFilePath = FileProviderFactory.create()
                                        .getFilePath(
                                            fileName,
                                            fileData.fileType
                                        )
                                    
                                    
                                    val sendFile = FileProviderFactory.create().uploadCipherFile(
                                        "file/upload",
                                        absltPath.fileAbsolutePath,
                                        fileData.fileType,
                                        fileName
                                    ) {
                                    
                                    }
                                    
                                    
                                    if (sendFile != null) {
                                        fileId = sendFile
                                    }
                                    
                                }
                                
                                
                            }
                            
                            
                        }
                        
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                    }
                }) {
                    Text("Отправить файл")
                }
                
                Button(onClick = {
                    try {
                        
                        val fileProviderFactory = FileProviderFactory.create()
                        
                        val cipherFilefileName = "cipherFileDec${Random.nextInt(0, 100000)}"
                        val decryptFilefileName = "decryptSuccess${Random.nextInt(0, 100000)}.pdf"
                        
                        
                        val cipherFilePath = fileProviderFactory
                            .getFilePath(
                                cipherFilefileName,
                                "pdf"
                            )
                        
                        val dectyptFilePath = fileProviderFactory
                            .getFilePath(
                                decryptFilefileName,
                                "pdf"
                            )
                        val url = "${EnvironmentConfig.serverUrl}file/id/$fileId"

//                        scope.launch {
//                            fileProviderFactory.downloadCipherFile(
//                                url,
//                                cipherFilePath,
//                                dectyptFilePath
//                            ) { newProgress ->
//
//                            }
//                        }
                        
                        
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                    }
                }) {
                    Text("Скачать файл")
                    
                    
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                }
            }
        }
    }
}



