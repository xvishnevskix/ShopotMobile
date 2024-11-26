package org.videotrade.shopot.presentation.screens.contacts


import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase

class ContactsViewModel() : ViewModel(),
    KoinComponent {
    val selectedContacts = mutableStateListOf<ContactDTO>()
    
    
    private val contactsUseCase: ContactsUseCase by inject()
    private val ProfileUseCase: ProfileUseCase by inject()
    
    val unregisteredContacts = contactsUseCase.unregisteredContacts
    
    private val _contacts = MutableStateFlow<List<ContactDTO>>(emptyList())
    
    //    val contacts: StateFlow<List<ContactDTO>> get() = _contacts
    val contacts = contactsUseCase.contacts
    
    
    fun fetchContacts() {
        viewModelScope.launch {
            
            
            val contactsSort = contactsUseCase.fetchContacts()
            
            if (contactsSort != null) {
                _contacts.value = contactsSort
            }
        }
    }
    
    
    fun getContacts() {
        viewModelScope.launch {
            
            
            val contactsSort = contactsUseCase.getContacts()
            
            println("contactsSort ${contactsSort}")
            
            if (contactsSort.isEmpty()) {
                fetchContacts()
            } else {
                
                _contacts.value = contactsSort
                
            }
        }
    }
    
    fun createChat(contact: ContactDTO, tabNavigator: TabNavigator) {
        viewModelScope.launch {
            val profile = ProfileUseCase.getProfile()
            
            contactsUseCase.createChat(profile.id, contact)
            
        }
        
    }
    
    fun createGroupChat(groupName: String) {
        viewModelScope.launch {
            val idUsers = selectedContacts.map { it.id }.toMutableList()
            idUsers.add(ProfileUseCase.getProfile().id)
            contactsUseCase.createGroupChat(idUsers, groupName)
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