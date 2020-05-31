package ru.skillbranch.devintensive.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_archive.*
import kotlinx.android.synthetic.main.item_chat_group.*
import kotlinx.android.synthetic.main.item_chat_single.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType

/**
 * Класс адаптера для чатов.
 */
class ChatAdapter(val listener: (ChatItem) -> Unit) :
        RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder>() {

    companion object {
        private const val ARCHIVE_TYPE = 0
        private const val SINGLE_TYPE = 1
        private const val GROUP_TYPE = 2
    }

    var items: List<ChatItem> = listOf()

    /**
     * Метод, возвращающий тип представления чата по типу чата.
     */
    override fun getItemViewType(position: Int):
            Int = when (items[position].chatType) {
        ChatType.ARCHIVE -> ARCHIVE_TYPE
        ChatType.SINGLE -> SINGLE_TYPE
        ChatType.GROUP -> GROUP_TYPE
    }

    /**
     * Реализация базовой функции адаптера.
     * В ней осуществляется выбор ViewHolder, исходя из типа объекта.
     */
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int):
            ChatItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            SINGLE_TYPE -> SingleViewHolder(inflater.inflate(R.layout.item_chat_single,
                    parent,
                    false))
            GROUP_TYPE -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group,
                    parent,
                    false))
            else -> ArchiveViewHolder(inflater.inflate(R.layout.item_chat_archive,
                    parent,
                    false))
        }
    }

    /**
     * Реализация базовой функции адаптера.
     * Возвращет количество элементов списка.
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Реализация базовой функции адаптера.
     * Метод для привязки элемента.
     */
    override fun onBindViewHolder(holder: ChatItemViewHolder,
                                  position: Int) {
        holder.bind(items[position], listener)
    }

    /**
     * Метод для обновления списка элемента.
     */
    fun updateData(data: List<ChatItem>) {

        val diffCallback = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int):
                    Boolean = items[oldItemPosition].id == data[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int):
                    Boolean = items[oldItemPosition].hashCode() == data[newItemPosition].hashCode()

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = data.size
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Абстрактный класс, описывающий сущность для адаптера.
     */
    abstract inner class ChatItemViewHolder(convertView: View) :
            RecyclerView.ViewHolder(convertView),
            LayoutContainer {

        override val containerView: View?
            get() = itemView

        abstract fun bind(item: ChatItem, listener:
        (ChatItem) -> Unit)
    }

    /**
     * Класс типа ViewHolder, описывющий сущность для чата.
     */
    inner class SingleViewHolder(itemView: View) :
            ChatItemViewHolder(itemView),
            ItemTouchViewHolder {

        override val containerView: View?
            get() = itemView

        /**
         * Метод связывания элементов.
         */
        override fun bind(item: ChatItem,
                          listener: (ChatItem) -> Unit) {

            // настройка аватара
            if (item.avatar == null) {
                Glide.with(itemView)
                        .clear(iv_avatar_single)
                iv_avatar_single.setInitials(item.initials)
            } else {
                Glide.with(itemView)
                        .load(item.avatar)
                        .into(iv_avatar_single)
            }

            // настройка индикатора
            sv_indicator.visibility = if (item.isOnline) View.VISIBLE else View.GONE

            // вывод даты последнего сообщения
            with(tv_date_single) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }

            // счетчик непрочитанных сообщений
            with(tv_counter_single) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            // заголовок
            tv_title_single.text = item.title
            // краткое описание
            tv_message_single.text = item.shortDescription

            // обработчик нажатия на элемент списка
            itemView.setOnClickListener { listener.invoke(item) }
        }

        /**
         * Метод-обработчик выбора элемента.
         */
        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        /**
         * Метод-обработчик свайпа элемента.
         */
        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    /**
     * Класс типа ViewHolder, описывющий сущность для  группового чата.
     */
    inner class GroupViewHolder(itemView: View) :
            ChatItemViewHolder(itemView),
            ItemTouchViewHolder {

        override val containerView: View?
            get() = itemView

        /**
         * Метод связывания элементов.
         */
        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {

            // аватарка группы
            iv_avatar_group.setInitials(item.initials)

            // последняя дата сообщения в группе
            with(tv_date_group) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }

            // счетчик непрочитанных сообщений
            with(tv_counter_group) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            // заголовок группы
            tv_title_group.text = item.title
            // краткое описание сообщения
            tv_message_group.text = item.shortDescription

            // автор последнего сообщения
            with(tv_message_author) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.author
            }
            // обработчик нажатия на элемент списка
            itemView.setOnClickListener { listener.invoke(item) }
        }

        /**
         * Метод-обработчик выбора элемента.
         */
        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        /**
         * Метод-обработчик свайпа элемента.
         */
        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    /**
     * Класс типа ViewHolder, описывющий сущность для архивного чата.
     */
    inner class ArchiveViewHolder(itemView: View) : ChatItemViewHolder(itemView) {

        /**
         * Метод связывания элементов.
         */
        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {

            // дата последнего сообщения
            tv_date_archive.apply {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }

            // колличество непрочитанных сообщений в архиве
            tv_counter_archive.apply {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            // заголовок архива
            tv_title_archive.text = item.title

            // краткое сообщение
            tv_message_archive.text = item.shortDescription

            // обработчик нажатия
            itemView.setOnClickListener {
                listener.invoke(item)
            }

            // автор последнего сообщения
            tv_message_author_archive.apply {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = "@${item.author}"
            }
        }
    }
}

