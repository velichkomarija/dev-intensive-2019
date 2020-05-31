package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

/**
 * Класс, описывающий Chat.
 */
data class Chat(
        val id: String,
        val title: String,
        val members: List<User> = listOf(),
        var messages: MutableList<BaseMessage> = mutableListOf(),
        var isArchived: Boolean = false
) {

    /**
     * Метод, расчитывающий число непрочитанных сообщений.
     */
    fun unreadableMessageCount(): Int {
        return messages
                .filter { !it.isReaded }
                .size
    }

    /**
     * Метод, возвращающий дату последнего сообщения.
     */
    fun lastMessageDate(): Date? {
        return messages.lastOrNull()?.date
    }

    /**
     * Короткое превью для последнего сообщения.
     */
    fun lastMessageShort(): Pair<String?, String?> =
            when (val lastMessage = messages.lastOrNull()) {
                is TextMessage -> lastMessage.text to "${lastMessage.from.firstName}"
                is ImageMessage -> "${lastMessage.from.firstName} - отправил фото" to "${lastMessage.from.firstName}"
                else -> "Сообщений еще нет" to ""
            }

    /**
     * Метод, проверяющий количество участников чата.
     */
    private fun isSingle(): Boolean = members.size == 1

    /**
     * Метод, приводящий экземпляр Chat к ChatItem.
     */
    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val user = members.first()
            ChatItem(
                    id,
                    user.avatar,
                    Utils.toInitials(user.firstName, user.lastName) ?: "??",
                    "${user.firstName ?: ""} ${user.lastName ?: ""}",
                    lastMessageShort().first,
                    unreadableMessageCount(),
                    lastMessageDate()?.shortFormat(),
                    user.isOnline
            )
        } else {
            ChatItem(
                    id,
                    null,
                    "",
                    title,
                    lastMessageShort().first,
                    unreadableMessageCount(),
                    lastMessageDate()?.shortFormat(),
                    false,
                    ChatType.GROUP,
                    lastMessageShort().second
            )
        }
    }
}

/**
 * Перечисление для типов чата.
 */
enum class ChatType {
    SINGLE,
    GROUP,
    ARCHIVE
}



