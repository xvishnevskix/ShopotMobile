package org.videotrade.shopot.presentation.screens.chat


import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.vinceglb.filekit.core.PickerType
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import org.videotrade.shopot.domain.model.GroupUserDTO
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.FileProviderFactory
import kotlin.random.Random

class ChatViewModel : ViewModel(), KoinComponent {
    private val chatUseCase: ChatUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()
    private val contactsUseCase: ContactsUseCase by inject()

    val groupUsers = MutableStateFlow<List<GroupUserDTO>>(listOf())

    private val _messages = MutableStateFlow<List<MessageItem>>(listOf())

    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()

    val profile = MutableStateFlow(ProfileDTO())


    val _currentChat = MutableStateFlow<ChatItem?>(null)
    val currentChat: StateFlow<ChatItem?> get() = _currentChat.asStateFlow()

    val ws = MutableStateFlow<DefaultClientWebSocketSession?>(null)


    val audioRecorder = MutableStateFlow(AudioFactory.createAudioRecorder())

    var isRecording = MutableStateFlow(false)


    var downloadProgress = MutableStateFlow(0f)


    val isScaffoldState = MutableStateFlow(false)

    val forwardMessage = MutableStateFlow<MessageItem?>(null)


    private val _selectedMessagesByChat = MutableStateFlow<Map<String, Pair<MessageItem?, String?>>>(emptyMap())
    val selectedMessagesByChat: StateFlow<Map<String, Pair<MessageItem?, String?>>> = _selectedMessagesByChat.asStateFlow()


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
        isScaffoldState.value = state
    }

    fun setCurrentChat(chat: ChatItem) {
        _currentChat.value = chat
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

    fun setCount(count: Int) {
        viewModelScope.launch {
            chatUseCase.setCount(count)
        }
    }

    fun implementCount() {
        viewModelScope.launch {
            chatUseCase.implementCount()
        }
    }


    fun sendMessage(
        content: String? = null,
        fromUser: String,
        chatId: String,
        notificationToken: String?,
        attachments: List<String>? = null,
        login: String? = null,
        isCipher: Boolean,
        forwardMessage: Boolean = false
    ) {
        viewModelScope.launch {
            var contentSort = ""


            if (content !== null && isCipher) {
                val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()

                val resEncups = encupsMessage(content, cipherWrapper)

                contentSort = Json.encodeToString(resEncups)
            } else {
                contentSort = content!!
            }

            chatUseCase.sendMessage(
                MessageItem(
                    content = contentSort,
                    fromUser = fromUser,
                    chatId = chatId,
                    anotherRead = false,
                    iread = false,
                    attachments = null,
                    forwardMessage = forwardMessage,
                ),
                attachments,
                selectedMessagesByChat.value[chatId]?.first?.id
            )
            println("сообщениесообщениесообщениесообщение")
            sendNotify("$login", content, notificationToken)
        }
    }


    fun sendAttachments(
        content: String?,
        fromUser: String,
        chatId: String,
        contentType: String,
        fileName: String,
        fileDir: String
    ) {
        viewModelScope.launch {
            val fileId = origin().sendFile(
                fileDir,
                contentType,
                fileName,
                false,
            )

            if (fileId !== null)
                sendMessage(
                    content = content,
                    fromUser = fromUser,
                    chatId = chatId,
                    notificationToken = null,
                    attachments = listOf(fileId),
                    isCipher = false
                )
        }
    }

    fun sendLargeFileAttachments(
        content: String? = null,
        fromUser: String,
        chatId: String,
        uploadId: String,
        fileId: String
    ) {
        viewModelScope.launch {
            chatUseCase.sendUploadMessage(
                MessageItem(
                    content = content,
                    fromUser = fromUser,
                    chatId = chatId,
                    uploadId = uploadId,
                    anotherRead = false,
                    iread = false,
                    attachments = null
                ),
                listOf(fileId)
            )
        }
    }


    fun sendNotify(
        title: String,
        content: String? = "Уведомление",
        notificationToken: String?
    ) {
        viewModelScope.launch {
            println("Уведомление ${notificationToken}")

            if (notificationToken !== null) {
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("title", title)
                        put("body", content)
                        put("notificationToken", notificationToken)

                    }
                )

                println("Уведомление ${jsonContent}")

                origin().post("notification/notify", jsonContent)
            }
        }
    }

    fun sendForwardMessage(
        messageId: String,
        chatId:String,
    ) {
        viewModelScope.launch {
            try {
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("action", "forwardMessage")
                        put("chatId",chatId)
                        put("messageId", messageId)
                        put("userId", profileUseCase.getProfile().id)
                    }
                )
                println("jsonContent $jsonContent")
                wsUseCase.wsSession.value?.send(Frame.Text(jsonContent))

            } catch (e: Exception) {
                println("Failed to send message: ${e.message}")
            }
        }
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


    fun sendVoice(fileDir: String, chat: ChatItem, voiceName: String) {
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
    }


    fun sendImage(
        content: String? = null,
        fromUser: String,
        chatId: String,
        uploadId: String,
        fileId: String
    ) {
        viewModelScope.launch {
            val filePick = FileProviderFactory.create()
                .pickFile(PickerType.Image)
            if (filePick !== null) {
                sendAttachments(
                    content,
                    fromUser,
                    chatId,
                    "image",
                    filePick.fileName,
                    filePick.fileAbsolutePath,
                )

            }
        }

    }


    fun findContactByPhone(phone: String): ContactDTO? {
        return contactsUseCase.contacts.value.find { it.phone == phone }
    }

    fun loadGroupUsers(chatId: String) {

        viewModelScope.launch {
            try {
                val groupUsersGet =
                    origin().get<List<GroupUserDTO>>("group_chat/chatParticipants?chatId=$chatId")

                val groupUsersFilter = mutableListOf<GroupUserDTO>()

                if (groupUsersGet != null) {
                    for (groupUser in groupUsersGet) {
                        fun normalizePhoneNumber(phone: String): String {
                            return phone.replace(Regex("[^0-9]"), "")
                        }

                        if (profile.value.phone == normalizePhoneNumber(groupUser.phone)) {
                            groupUsersFilter.add(
                                groupUser.copy(
                                    firstName = "Вы",
                                    lastName = ""
                                )
                            )
                            continue
                        }

                        val findInContacts = contactsUseCase.contacts.value.find {
                            normalizePhoneNumber(it.phone) == normalizePhoneNumber(groupUser.phone)
                        }

                        if (findInContacts !== null && findInContacts.firstName !== null && findInContacts.lastName !== null) {
                            groupUsersFilter.add(
                                groupUser.copy(
                                    firstName = findInContacts.firstName,
                                    lastName = findInContacts.lastName
                                )
                            )
                        } else {
                            groupUsersFilter.add(groupUser)
                        }

                    }
                }

                groupUsers.value = groupUsersFilter

            } catch (e: Exception) {
            }
        }

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


}

