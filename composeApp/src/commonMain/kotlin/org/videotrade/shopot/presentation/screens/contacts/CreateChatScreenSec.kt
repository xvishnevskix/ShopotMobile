package org.videotrade.shopot.presentation.screens.contacts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Common.SafeArea


class CreateChatScreenSec() : Screen {
    
    
    @Composable
    override fun Content() {
        val viewModel: ContactsViewModel = koinInject()
        val contacts = viewModel.contacts.collectAsState(initial = listOf()).value
        
        
        viewModel.fetchContacts()
        
        
        SafeArea {
            
            LazyColumn {
                
                itemsIndexed(contacts) { index, item ->
                    Text("${item.name} ${item.phoneNumber}")
                    
                }
            }
        }
        
    }
}

