package org.videotrade.shopot.presentation.screens.contacts


import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.multiplatform.Contact
import org.videotrade.shopot.multiplatform.ContactsProviderFactory

class ContactsViewModel() : ViewModel(),
    KoinComponent {
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> get() = _contacts
    
    fun fetchContacts() {
        viewModelScope.launch {
            
            
            _contacts.value = ContactsProviderFactory.create().getContacts()
            
            
        }
    }
}
