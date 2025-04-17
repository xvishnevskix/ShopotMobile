package org.videotrade.shopot.presentation.screens.chat


import androidx.compose.runtime.mutableStateMapOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.encupsMessage
import org.videotrade.shopot.api.getCurrentTimeList
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.model.GroupInfo
import org.videotrade.shopot.domain.model.GroupUserDTO
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.StickerPack
import org.videotrade.shopot.domain.model.WsReconnectionCase
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.StickerUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.MusicType
import org.videotrade.shopot.multiplatform.PlatformFilePick
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.test.sendMessageOrReconnect
import kotlin.random.Random

class ChatViewModel : ViewModel(), KoinComponent {
    private val chatUseCase: ChatUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()
    private val contactsUseCase: ContactsUseCase by inject()
    private val musicPlayer = AudioFactory.createMusicPlayer()
    
    val currentChat = chatUseCase.currentChat
    
    val footerText = MutableStateFlow("")
    
    private val _messages = MutableStateFlow<List<MessageItem>>(listOf())
    
    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()
    
    val profile = MutableStateFlow(ProfileDTO())
    
    
    val ws = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    
    
    val audioRecorder = MutableStateFlow(AudioFactory.createAudioRecorder())
    
    var isRecording = MutableStateFlow(false)
    
    
    var downloadProgress = MutableStateFlow(0f)
    
    
    val isScaffoldForwardState = MutableStateFlow(false)
    val isScaffoldStickerState = MutableStateFlow(false)
    
    val forwardMessage = MutableStateFlow<MessageItem?>(null)
    
    
    private val _selectedMessagesByChat =
        MutableStateFlow<Map<String, Pair<MessageItem?, String?>>>(emptyMap())
    val selectedMessagesByChat: StateFlow<Map<String, Pair<MessageItem?, String?>>> =
        _selectedMessagesByChat.asStateFlow()
    
    private val _boxHeight = MutableStateFlow(0)
    val boxHeight: StateFlow<Int> = _boxHeight
    
    private val _isDeleteConfirmationVisible = MutableStateFlow(false)
    val isDeleteConfirmationVisible: StateFlow<Boolean> = _isDeleteConfirmationVisible
    
    
    private val _messageToDelete = MutableStateFlow<MessageItem?>(null)
    val messageToDelete: StateFlow<MessageItem?> = _messageToDelete
    
    private val _currentPlayingMessage = MutableStateFlow<String?>(null)
    val currentPlayingMessage: StateFlow<String?> = _currentPlayingMessage
    
    private val _voiceMessages = MutableStateFlow<List<MessageItem>>(emptyList())
    val voiceMessages: StateFlow<List<MessageItem>> = _voiceMessages
    
    
    init {
        
        
        viewModelScope.launch {
            
            
            chatUseCase.getMessages().collect {
                
                println("it313123131 $it")
                _messages.value = it
            }
            
            
        }
    }
    
    fun setForwardMessage(message: MessageItem) {
        forwardMessage.value = message
    }
    
    fun setScaffoldState(state: Boolean) {
        isScaffoldForwardState.value = state
    }
    
    fun setScaffoldStickerState(state: Boolean) {
        isScaffoldForwardState.value = state
    }
    
    fun setCurrentChat(chat: ChatItem) {
        chatUseCase.setCurrentChat(chat)
    }
    
    fun setIsRecording(isRecordingNew: Boolean) {
        isRecording.value = isRecordingNew
    }
    
    fun sendReadMessage(messageId: String) {
        viewModelScope.launch {
            chatUseCase.sendReadMessage(messageId, profile.value.id)
        }
    }
    
    
    fun getMessagesBack(chatId: String) {
        viewModelScope.launch {
            chatUseCase.getMessagesBack(chatId)
        }
    }
    
    fun setMessagePage(page: Int) {
        viewModelScope.launch {
            chatUseCase.setMessagePage(page)
        }
    }
    
    fun implementCount() {
        viewModelScope.launch {
            chatUseCase.implementCount()
        }
    }
    
    //обновить высоту заблюренного сообщения
    fun updateBoxHeight(height: Int) {
        _boxHeight.value = height
    }
    
    
    // Установить сообщение для удаления
    fun showDeleteConfirmation(message: MessageItem) {
        _messageToDelete.value = message
        _isDeleteConfirmationVisible.value = true
        
    }
    
    // Скрыть окно подтверждения
    fun dismissDeleteConfirmation() {
        _messageToDelete.value = null
        _isDeleteConfirmationVisible.value = false
    }
    
    // Удалить сообщение и закрыть окно
    fun deleteMessageAndDismiss(onDismiss: () -> Unit) {
        _messageToDelete.value?.let {
            deleteMessage(it) // Вызов существующей функции удаления
        }
        dismissDeleteConfirmation()
        onDismiss()
    }
    
    
    fun setPlayingMessage(messageId: String?) {
        _currentPlayingMessage.value = messageId
    }
    
    
    fun addFileMessage(
        chat: ChatItem,
        fileType: String,
        filePick: PlatformFilePick,
        photoPath: String? = null,
        photoName: String? = null,
    ) {
        
        println("filePick.fileName ${filePick.fileName}")
        addMessage(
            MessageItem(
                Random.nextInt(1, 1501).toString(),
                profile.value.id,
                "",
                false,
                null,
                0,
                getCurrentTimeList(),
                false,
                chat.id,
                false,
                true,
                listOf(
                    Attachment(
                        Random.nextInt(1, 501).toString(),
                        Random.nextInt(1, 501).toString(),
                        profile.value.id,
                        Random.nextInt(1, 501).toString(),
                        fileType,
                        filePick.fileName,
                        originalFileDir = filePick.fileAbsolutePath,
                        size = filePick.fileSize,
                        photoPath = photoPath,
                        photoName = photoName,
                    )
                ),
                upload = true,
                uploadId = Random.nextInt(1, 1501).toString()
            )
        )
        
        
    }
    
    
    fun addMessage(message: MessageItem) {
        
        chatUseCase.addMessage(message)
    }
    
    fun deleteMessage(message: MessageItem) {
        viewModelScope.launch {
            chatUseCase.delMessage(message)
        }
    }
    
    fun clearMessages() {
        viewModelScope.launch {
            chatUseCase.clearMessages()
        }
    }
    
    fun getProfile() {
        viewModelScope.launch {
            profile.value = profileUseCase.getProfile()
        }
    }
    
    
    fun sendMessage(
        content: String? = null,
        attachments: List<String>? = null,
        isCipher: Boolean,
        forwardMessage: Boolean = false,
        chat: ChatItem,
    ) {
        viewModelScope.launch {
            var contentSort = ""
            
            val commonViewModel: CommonViewModel = KoinPlatform.getKoin().get()
            
            
            if (content !== null && isCipher) {
                val resEncups = encupsMessage(content)
                
                contentSort = Json.encodeToString(resEncups)
            } else {
                contentSort = content!!
            }
            
            chatUseCase.sendMessage(
                MessageItem(
                    content = contentSort,
                    fromUser = profile.value.id,
                    chatId = chat.chatId,
                    anotherRead = false,
                    iread = false,
                    attachments = null,
                    forwardMessage = forwardMessage,
                ),
                attachments,
                selectedMessagesByChat.value[chat.chatId]?.first?.id
            )
            println("сообщениесообщениесообщениесообщение")
            
            musicPlayer.play("message", false, MusicType.Notification)
            commonViewModel.sendNotify(
                "+${profile.value.phone}",
                content,
                profile.value.id,
                chat.chatId,
                chat.personal
            )
            
            clearSelection(
                chat.chatId,
            )
        }
    }
    
    
    fun sendLargeFileAttachments(
        content: String? = null,
        uploadId: String,
        fileIds: List<String>,
        fileType: String,
        chat: ChatItem,
    ) {
        val commonViewModel: CommonViewModel = KoinPlatform.getKoin().get()
        
        viewModelScope.launch {
            chatUseCase.sendUploadMessage(
                MessageItem(
                    content = content,
                    fromUser = profile.value.id,
                    chatId = chat.chatId,
                    uploadId = uploadId,
                    anotherRead = false,
                    iread = false,
                    attachments = null
                ),
                fileIds,
                selectedMessagesByChat.value[chat.chatId]?.first?.id,
                fileType
            
            )
            
            commonViewModel.sendNotify(
                "+${profile.value.phone}",
                "Отправлен файл",
                profile.value.id,
                chat.chatId,
                chat.personal
            )
            
            musicPlayer.play("message", false, MusicType.Notification)
            
        }
    }
    
    fun sendVoice(chat: ChatItem, voiceName: String) {
        val fileDir = audioRecorder.value.stopRecording(true) ?: return
        
        val fileSize =
            FileProviderFactory.create().getFileSizeFromUri(fileDir)
        
        println("fileSize ${fileSize}")

//        if(fileSize !== null)
        addMessage(
            MessageItem(
                Random.nextInt(1, 2001).toString(),
                profile.value.id,
                "",
                false,
                null,
                0,
                getCurrentTimeList(),
                false,
                chat.id,
                false,
                true,
                listOf(
                    Attachment(
                        Random.nextInt(1, 501).toString(),
                        Random.nextInt(1, 501).toString(),
                        profile.value.id,
                        Random.nextInt(1, 501).toString(),
                        "audio/mp4",
                        voiceName,
                        originalFileDir = fileDir,
//                        fileSize
                    )
                ),
                upload = true,
                uploadId = Random.nextInt(1, 501).toString()
            )
        )
        
        musicPlayer.play("message", false, MusicType.Notification)
        
    }
    
    
    fun sendImage(
        content: String? = null,
        fileName: String,
        fileAbsolutePath: String,
        contentType: String,
        chat: ChatItem
    ) {
        viewModelScope.launch {
//            val filePick = FileProviderFactory.create()
//                .pickFile(PickerType.Image)
            
            
            sendAttachments(
                content,
                contentType,
                fileName,
                fileAbsolutePath,
                chat
            )
            
        }
        
    }
    
    private fun sendAttachments(
        content: String?,
        contentType: String,
        fileName: String,
        fileDir: String,
        chat: ChatItem,
    ) {
        viewModelScope.launch {
            
            val fileId = origin().sendImageFile(
                fileDir,
                contentType,
                fileName,
                false,
            )
            
            if (fileId !== null)
                sendMessage(
                    content = content,
                    attachments = listOf(fileId),
                    isCipher = false,
                    chat = chat
                )
        }
    }
    
    fun sendStickerMessage(
        chat: ChatItem,
        stickerId: String
    ) {
        // Добавляем сообщение с стикером
        
        if (currentChat.value !== null) {
            sendMessage(
                content = footerText.value,
                attachments = listOf(stickerId),
                isCipher = false,
                chat = chat,
            )
        }
        
    }
    
    fun sendForwardMessage(
        messageId: String,
        chatId: String,
    ) {
        viewModelScope.launch {
            try {
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("action", "forwardMessage")
                        put("chatId", chatId)
                        put("messageId", messageId)
                        put("userId", profileUseCase.getProfile().id)
                        put("fileType", forwardMessage.value?.attachments?.get(0)?.type)
                    }
                )
                println("jsonContent $jsonContent")
                
                sendMessageOrReconnect(
                    wsUseCase.wsSession.value,
                    jsonContent,
                    WsReconnectionCase.ChatWs
                )
                
                musicPlayer.play("message", false, MusicType.Notification)
                
            } catch (e: Exception) {
                println("Failed to send message: ${e.message}")
            }
        }
    }
    
    fun findContactByPhone(phone: String): ContactDTO? {
        return contactsUseCase.contacts.value.find { it.phone == phone }
    }
    
    
    fun selectMessage(chatId: String, message: MessageItem, senderName: String) {
        _selectedMessagesByChat.value = _selectedMessagesByChat.value.toMutableMap().apply {
            this[chatId] = Pair(message, senderName)
        }
    }
    
    fun clearSelection(chatId: String) {
        _selectedMessagesByChat.value = _selectedMessagesByChat.value.toMutableMap().apply {
            this[chatId] = Pair(null, null)
        }
    }
    
    
    ////////////////// СТИКЕРЫ /////////////////////////////
    
    private val stickerUseCase = StickerUseCase()
    
    private val _stickerPacks = MutableStateFlow<List<StickerPack>>(emptyList())
    val stickerPacks: StateFlow<List<StickerPack>> get() = _stickerPacks
    
    private val _favoriteStickerPacks = MutableStateFlow<List<StickerPack>>(emptyList())
    val favoriteStickerPacks: StateFlow<List<StickerPack>> = _favoriteStickerPacks
    
    private val _stickerPack =
        MutableStateFlow(StickerPack("", "", false, emptyList()))  // Исправление здесь
    val stickerPack: StateFlow<StickerPack> get() = _stickerPack
    
    private var currentPage = 0
    private val pageSize = 2
    private var isLastPage = false
    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    
    fun downloadStickerPacks(reset: Boolean = false) {
        if (_isLoading.value || isLastPage) return
        
        _isLoading.value = true
        viewModelScope.launch {
            if (reset) {
                _stickerPacks.value = emptyList()
                currentPage = 0
                isLastPage = false
            }
            
            val packs = stickerUseCase.downloadStickerPacks(currentPage, pageSize)
            if (packs.isNullOrEmpty()) {
                isLastPage = true
            } else {
                currentPage++
                _stickerPacks.value = _stickerPacks.value + packs
            }
            _isLoading.value = false
        }
    }
    
    fun getFavoritePacks() {
        viewModelScope.launch {
            // Получаем список избранных пакетов (из базы данных или API)
            val favoritePackList = stickerUseCase.getFavoritePacks()
            
            // Загружаем информацию о каждом пакете по ID
            val packs = favoritePackList?.mapNotNull { favoritePack ->
                getPack(favoritePack.packId) // Метод для загрузки пакета по его ID
            }
            
            _favoriteStickerPacks.value = packs ?: emptyList() // Обновляем состояние
        }
    }
    
    // Функция для получения пакета по его ID
    private suspend fun getPack(packId: String): StickerPack? {
        return stickerUseCase.getPack(packId)
    }
    
    fun removePackFromFavorites(packId: String) {
        viewModelScope.launch {
            val success = stickerUseCase.removePackFromFavorites(packId)
            if (success) {
                
                _stickerPacks.value = _stickerPacks.value.map { pack ->
                    if (pack.packId == packId) pack.copy(favorite = false) else pack
                }
                
                _favoriteStickerPacks.value =
                    _favoriteStickerPacks.value.filter { it.packId != packId }
                
                println("Pack successfully removed from favorites")
            } else {
                println("Failed to remove pack from favorites")
            }
        }
    }
    
    fun addPackToFavorites(packId: String) {
        viewModelScope.launch {
            val success = stickerUseCase.addPackToFavorites(packId)
            if (success) {
                _stickerPacks.value = _stickerPacks.value.map { pack ->
                    if (pack.packId == packId) pack.copy(favorite = true) else pack
                }
                println("Pack successfully added to favorites")
            } else {
                println("Failed to add pack to favorites")
            }
        }
    }
    
    
    /////////////////////////СТАТУСЫ//////////////////////////////
    
    val userStatuses: StateFlow<Map<String, String>> =
        chatUseCase.userStatuses
            .map { map -> map.mapValues { it.value.first } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())
    
    
    fun onTypingStart() = viewModelScope.launch { chatUseCase.sendTypingStart() }
    fun onTypingEnd() = viewModelScope.launch { chatUseCase.sendTypingEnd() }
    fun onFileUploadStart() = viewModelScope.launch { chatUseCase.sendFileUploadStart() }
    fun onFileUploadEnd() = viewModelScope.launch { chatUseCase.sendFileUploadEnd() }
    fun onStickerChoosingStart() = viewModelScope.launch { chatUseCase.sendStickerChoosingStart() }
    fun onStickerChoosingEnd() = viewModelScope.launch { chatUseCase.sendStickerChoosingEnd() }
    fun onVoiceRecordingStart() = viewModelScope.launch { chatUseCase.sendVoiceRecordingStart() }
    fun onVoiceRecordingEnd() = viewModelScope.launch { chatUseCase.sendVoiceRecordingEnd() }
    
    
    ///////////////////////////////////////////////////////
    
    
}

