package ru.skillbranch.devintensive.models.data

/**
 * Дата класс, описывающий сущность чата для отображения.
 */
data class ChatItem (
    val id: String,
    val avatar: String?,
    val initials: String,
    val title: String,
    val shortDescription: String?,
    val messageCount: Int = 0,
    val lastMessageDate: String?,
    val isOnline: Boolean = false,
    val chatType : ChatType = ChatType.SINGLE,
    var author :String? = null
) {

}