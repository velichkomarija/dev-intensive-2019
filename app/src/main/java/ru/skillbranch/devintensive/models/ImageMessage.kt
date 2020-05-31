package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import java.util.*

/**
 * Метод, описывающий сущность сообщения с изображением.
 */
class ImageMessage(
        id: String,
        from: User,
        chat: Chat,
        isIncoming: Boolean = false,
        date: Date = Date(),
        isReaded: Boolean = false,
        var image: String
) : BaseMessage(id, from, chat, isIncoming, date, isReaded)