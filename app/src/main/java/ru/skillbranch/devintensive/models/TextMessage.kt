package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import java.util.*

/**
 * Сущность, описывающая текстовое сообщение.
 */
class TextMessage(
        id: String,
        from: User,
        chat: Chat,
        isIncoming: Boolean = false,
        date: Date = Date(),
        isReaded:Boolean = false,
        var text: String?
) : BaseMessage(id, from, chat, isIncoming, date, isReaded)

