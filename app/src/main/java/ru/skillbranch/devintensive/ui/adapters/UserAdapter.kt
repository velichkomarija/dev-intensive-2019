package ru.skillbranch.devintensive.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_single.sv_indicator
import kotlinx.android.synthetic.main.item_user_list.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.UserItem

/**
 * Адаптер для списка пользователей.
 */
class UserAdapter(val listener: (UserItem) -> Unit) :
        RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var items: List<UserItem> = listOf()

    /**
     * Реализация базовой функции адаптера.
     */
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int):
            UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val convertView = inflater.inflate(R.layout.item_user_list, parent, false)
        return UserViewHolder(convertView)
    }

    /**
     * Реализация базовой функции адаптера.
     * Возвращает количество элементов.
     */
    override fun getItemCount(): Int = items.size

    /**
     * Реализация базовой функции адаптера.
     */
    override fun onBindViewHolder(holder: UserViewHolder,
                                  position: Int) = holder.bind(items[position], listener)

    /**
     * Метод обновления данных для адаптера.
     */
    fun updateData(data: List<UserItem>) {

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
     * Класс реализующий RecyclerView.ViewHolder.
     */
    inner class UserViewHolder(convertView: View) :
            RecyclerView.ViewHolder(convertView),
            LayoutContainer {

        override val containerView: View?
            get() = itemView

        /**
         * Метод связывания элементов.
         */
        fun bind(user: UserItem, listener: (UserItem) -> Unit) {

            // загрузка аватара
            if (user.avatar != null) {
                Glide.with(itemView)
                        .load(user.avatar)
                        .into(iv_avatar_user)
            } else {
                Glide.with(itemView)
                        .clear(iv_avatar_user)
                iv_avatar_user.setInitials(user.initials ?: "??")
            }

            // индикатор онлайна
            sv_indicator.visibility = if (user.isOnline) View.VISIBLE else View.GONE
            // полное имя
            tv_user_name.text = user.fullName
            // дата последней активности
            tv_last_activity.text = user.lastActivity
            // значок выбора элемента
            iv_selected.visibility = if (user.isSelected) View.VISIBLE else View.GONE

            // приявзяка обработчика нажатий
            itemView.setOnClickListener {
                listener.invoke(user)
            }
        }
    }
}