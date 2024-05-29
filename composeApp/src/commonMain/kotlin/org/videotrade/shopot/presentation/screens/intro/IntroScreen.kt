package org.videotrade.shopot.presentation.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ktor.http.HttpStatusCode
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.logo


class IntroScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: IntroViewModel = koinInject()
        
        
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                
                )
        }
        
        
        LaunchedEffect(key1 = Unit) {
            
            
            val contactsNative = PermissionsProviderFactory.create().getPermission("contacts")
            
            
            if (!contactsNative) return@LaunchedEffect
            
            
            val response = origin().reloadTokens()
            
            if (response != null && response.status == HttpStatusCode.OK) {
                
                
                viewModel.fetchContacts(navigator)
                return@LaunchedEffect
                
                
            }
            
            
            navigator.push(SignInScreen())
            
            
        }
        
        
    }
    
    
}