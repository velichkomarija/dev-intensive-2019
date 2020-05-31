package ru.skillbranch.devintensive.models.data

/**
 * Класс, описывающий сущность User, для отображения в интерфейсе.
 */
data class UserItem (
    val id: String,
    val fullName: String,
    val initials : String?,
    val avatar: String?,
    var lastActivity:String,
    var isSelected : Boolean = false,
    var isOnline: Boolean = false
)