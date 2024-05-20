package org.videotrade.shopot.presentation.screens.contacts


import androidx.compose.runtime.mutableStateListOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.multiplatform.ContactsProviderFactory

class ContactsViewModel() : ViewModel(),
    KoinComponent {
    private val _contacts = MutableStateFlow<List<ContactDTO>>(emptyList())
    val contacts: StateFlow<List<ContactDTO>> get() = _contacts
    
    fun fetchContacts() {
        viewModelScope.launch {
            
            
            _contacts.value = ContactsProviderFactory.create().getContacts()
            
            
        }
    }
}


class SharedViewModel : ViewModel() {
    val selectedContacts = mutableStateListOf<ContactDTO>()

    fun addContact(contact: ContactDTO) {
        if (!selectedContacts.contains(contact)) {
            selectedContacts.add(contact)
        }
    }

    fun removeContact(contact: ContactDTO) {
        selectedContacts.remove(contact)
    }
}