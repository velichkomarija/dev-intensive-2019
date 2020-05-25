package ru.skillbranch.devintensive.repositories

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.utils.DataGenerator

object ChatRepository {

    private val chats = CacheManager.loadChats()

    fun loadChats() : MutableLiveData<List<Chat>>{
        return chats
    }

    fun update(chat: Chat) {
        val copy =  chats.value!!.toMutableList()
        val identificator = chats.value!!.indexOfFirst { it.id == chat.id }

        if(identificator == -1) return
        copy[identificator] = chat
        chats.value = copy
    }

    fun find(id: String): Chat? {
        val indetificator = chats.value!!.indexOfFirst { it.id == id}
        return chats.value!!.getOrNull(indetificator)
    }
}






