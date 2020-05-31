package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.repositories.ChatRepository
import ru.skillbranch.devintensive.utils.DataGenerator

class MainViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->
        val archived = chats.filter { it.isArchived }
        if(archived.isEmpty()) {
            return@map chats.map { it.toChatItem() }
        } else {
            val listWithArchive = mutableListOf<ChatItem>()
            listWithArchive.add(0, makeArchiveItem(archived))
            listWithArchive.addAll((chats.filter { !it.isArchived }.map { it.toChatItem() }))
            return@map listWithArchive
        }
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterF = {
            val queryStr = query.value!!
            val chats2 = chats.value!!

            result.value = if (queryStr.isEmpty()) chats2 else chats2.filter { it.title.contains(queryStr, true)}
        }

        result.addSource(chats){ filterF.invoke() }
        result.addSource(query){ filterF.invoke() }

        return result
    }

    fun addToArchive(id: String) {
        val chat = chatRepository.find(id)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(id: String) {
        val chat = chatRepository.find(id)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }

    private fun makeArchiveItem(archived : List<Chat>) : ChatItem {
        val count = archived.fold(0) { acc, chat -> acc + chat.unreadableMessageCount() }

        val lastChat: Chat = if (archived.none { it.unreadableMessageCount() != 0 }) archived.last() else
            archived.filter { it.unreadableMessageCount() != 0 }.maxBy { it.lastMessageDate()!! }!!

        return ChatItem(
                "-1",
                null,
                "",
                "Архив чатов",
                lastChat.lastMessageShort().first,
                count,
                lastChat.lastMessageDate()?.shortFormat(),
                false,
                ChatType.ARCHIVE,
                lastChat.lastMessageShort().second
        )
    }
}