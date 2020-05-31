package ru.skillbranch.devintensive.repositories

import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.utils.DataGenerator

/**
 * Класс, имитирующий БД для групповых чатов.
 */
object GroupRepository {

    /**
     * Метод, возвращающий список пользователей чата.
     */
    fun loadUsers(): List<User> = DataGenerator.stabUsers

    /**
     * Метод, создающий групповой чат.
     */
    fun createChat(items: List<UserItem>) {
        val ids = items.map { it.id }
        val users = CacheManager.findUsersById(ids)
        val title = users
                .map { it.firstName }
                .joinToString { "," }

        val chat = Chat(
                CacheManager.nextChatId(),
                title,
                users
        )

        CacheManager.innerChat(chat)
    }
}