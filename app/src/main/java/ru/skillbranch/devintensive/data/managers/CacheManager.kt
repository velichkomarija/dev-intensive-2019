package ru.skillbranch.devintensive.data.managers

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import ru.skillbranch.devintensive.utils.DataGenerator

/**
 * Эмулятор БД.
 */
object CacheManager {
    private val chats = mutableLiveData(DataGenerator.stabChats)
    private val users = mutableLiveData(DataGenerator.stabUsers)

    /**
     * Метод, подгружающий список чатов.
     */
    fun loadChats(): MutableLiveData<List<Chat>> {
        return chats
    }

    /**
     * Метод, поиска пользователя по id.
     */
    fun findUsersById(ids: List<String>): List<User> {
        return users.value!!
                .filter {
                    ids.contains(it.id)
                }
    }

    /**
     * Метод, возвращающий идентификатор следующего чата.
     */
    fun nextChatId(): String {
        return "${chats.value!!.size}"
    }

    /**
     * Метод добавления нового чата в CacheManager.
     */
    fun innerChat(chat: Chat) {
        val copy = chats.value!!.toMutableList()
        copy.add(chat)
        chats.value = copy
    }

}
