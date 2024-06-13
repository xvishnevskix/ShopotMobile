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
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.ToasterViewModel
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import org.videotrade.shopot.presentation.screens.permissions.PermissionsScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.logo


class IntroScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: IntroViewModel = koinInject()
        val toasterViewModel: ToasterViewModel = koinInject()
        
        
        
        
        LaunchedEffect(key1 = Unit) {
            try {
                
                
                viewModel.navigator.value = navigator
                
                
                val contactsNative = PermissionsProviderFactory.create().checkPermission("contacts")
                
                
                
                
                if (!contactsNative) {
//                    toasterViewModel.toaster.show("Добавьте все разрешения")
                    
                    navigator.replace(PermissionsScreen())
                    return@LaunchedEffect
                }
                
                
                val response = origin().reloadTokens()
                
                
                
                
                if (response != null && response.status == HttpStatusCode.OK) {
                    
                    
                    viewModel.updateNotificationToken()
                    
                    
                    viewModel.fetchContacts(navigator)
                    
                    
                    
                    
                    return@LaunchedEffect
                    
                    
                }
                
                
                navigator.replace(SignInScreen())
                
            } catch (e: Exception) {
                
                navigator.replace(SignInScreen())
                
            }
            
        }
        
        
        
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            SafeArea {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    
                    )
            }
        }
        
    }
    
    
}