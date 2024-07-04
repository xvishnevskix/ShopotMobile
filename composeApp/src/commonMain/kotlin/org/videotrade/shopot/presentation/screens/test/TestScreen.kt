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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import org.videotrade.shopot.multiplatform.decupsChachaMessage
import org.videotrade.shopot.multiplatform.encupsChachaMessage
import org.videotrade.shopot.multiplatform.sharedSecret

class TestScreen : Screen {
    @Composable
    override fun Content() {
        var publicKey by remember { mutableStateOf("opsda") }
        
        var errorMessage by remember { mutableStateOf("") }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(onClick = {
                try {
                    val publicKeyBytes = publicKey.toByteArray()
                    
                    val result = sharedSecret(publicKeyBytes)
                    
                    val result2 = encupsChachaMessage("privet", result[1])
                    
                    val result3 = decupsChachaMessage(
                        cipher = result2.cipher,
                        block = result2.block,
                        authTag = result2.authTag,
                        result[1]
                    )
                    
                    println("result3 $result3")
                    
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


