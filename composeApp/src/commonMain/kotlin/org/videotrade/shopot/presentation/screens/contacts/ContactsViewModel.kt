package org.videotrade.shopot.presentation.screens.contacts


import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase

class ContactsViewModel() : ViewModel(),
    KoinComponent {
    val selectedContacts = mutableStateListOf<ContactDTO>()
    
    
    private val ContactsUseCase: ContactsUseCase by inject()
    private val ProfileUseCase: ProfileUseCase by inject()
    
    
    private val _contacts = MutableStateFlow<List<ContactDTO>>(emptyList())
    
    val contacts: StateFlow<List<ContactDTO>> get() = _contacts
    
    fun fetchContacts() {
        viewModelScope.launch {
            
            
            val contactsSort = ContactsUseCase.fetchContacts()
            
            if (contactsSort != null) {
                _contacts.value = contactsSort
            }
        }
    }
    
    
    fun getContacts() {
        viewModelScope.launch {
            
            
            val contactsSort = ContactsUseCase.getContacts()
            
            if (contactsSort.isEmpty()) {
                fetchContacts()
            } else {
                
                _contacts.value = contactsSort
                
            }
        }
    }
    
    fun createChat(contact: ContactDTO, navigator: Navigator) {
        viewModelScope.launch {
            val profile = ProfileUseCase.getProfile()
            
            if (profile != null) {
                ContactsUseCase.createChat(profile.id, contact, navigator)
            }
            
        }
        
    }
    
    
    fun addContact(contact: ContactDTO) {
        if (!selectedContacts.contains(contact)) {
            selectedContacts.add(contact)
        }
    }
    
    fun removeContact(contact: ContactDTO) {
        selectedContacts.remove(contact)
    }
}