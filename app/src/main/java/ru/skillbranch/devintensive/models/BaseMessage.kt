package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import java.util.*

/**
 * Метод, описывающий сущность сообщения.
 */
abstract class BaseMessage(
        val id: String,
        val from: User,
        val chat: Chat,
        val isIncoming: Boolean = true,
        val date: Date = Date(),
        var isReaded: Boolean = false

)