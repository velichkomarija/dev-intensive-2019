package ru.skillbranch.devintensive.ui.group

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.ui.adapters.UserAdapter
import ru.skillbranch.devintensive.viewmodels.GroupViewModel

/**
 * Класс, описывающий активность для создания группового чата.
 */
class GroupActivity : AppCompatActivity() {

    private lateinit var usersAdapter: UserAdapter
    private lateinit var viewModel: GroupViewModel

    /**
     * Реализация базового метода ЖЦ по созданию активности.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        initToolbar()
        initViewModel()
        initViews()
    }

    /**
     * Метод создания контекстного меню для реализации функции поиска.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Метод обработчик выбора элемента контекстного меню
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            overridePendingTransition(R.anim.idle, R.anim.bottom_down)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Инициализация ViewModel.
     */
    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        viewModel.getUserData()
                .observe(this, Observer {
                    usersAdapter.updateData(it)
                })

        viewModel.getSelectedData()
                .observe(this, Observer {
                    updateChips(it)
                    toggleFab(it.size > 1)
                })
    }

    /**
     * Обработчик для отображения кнопки создания чата.
     */
    private fun toggleFab(isShow: Boolean) {
        if (isShow) fab_group.show()
        else fab_group.hide()
    }

    /**
     * Метод для инициализации всех графических элементов.
     */
    private fun initViews() {
        // адаптер списка
        usersAdapter = UserAdapter {
            viewModel.handleSelectedItem(it.id)
        }

        // разделитель элементов списка
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_chat_list, theme))

        // добавление адаптера к списку
        with(rv_user_list) {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(this@GroupActivity)
            addItemDecoration(divider)
        }

        // добавление обработчика нажатия на кнопку создания нового чата
        fab_group.setOnClickListener {
            viewModel.handleCreateGroup()
            finish()
            overridePendingTransition(R.anim.idle, R.anim.bottom_down)
        }
    }

    /**
     * Инициализация Toolbar.
     */
    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Добавление Chip в группу.
     */
    private fun addChipToGroup(user: UserItem) {
        val chip = Chip(this).apply {

            // загрузка аватара
            Glide.with(this).load(user.avatar)
                    .circleCrop()
                    .into(object : CustomTarget<Drawable>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            chipIcon = placeholder
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            chipIcon = drawAvatarForChip(user.initials ?: "??")
                        }

                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            chipIcon = resource
                        }
                    })

            text = user.fullName
            chipIcon = resources.getDrawable(R.drawable.avatar_default, theme)
            isCloseIconVisible = true
            tag = user.id
            isClickable = true
            closeIconTint = ColorStateList.valueOf(Color.WHITE)
            chipBackgroundColor = ColorStateList.valueOf(getColor(R.color.color_primary_light))
            setTextColor(Color.WHITE)
        }

        // обработчик нажатия на элемент с ценью его закрытия
        chip.setOnCloseIconClickListener { viewModel.handleRemoteChip(it.tag.toString()) }
        // добавление элемента в группу
        chip_group.addView(chip)
    }


    /**
     *      Обновление элемента типа Chip.
     */
    private fun updateChips(listUsers: List<UserItem>) {
        chip_group.visibility = if (listUsers.isEmpty()) View.GONE else View.VISIBLE

        val users = listUsers
                .associate { user -> user.id to user }
                .toMutableMap()

        val views = chip_group.children.associate { view -> view.tag to view }

        for ((k, v) in views) {
            if (users.containsKey(k)) chip_group.removeView(v)
            else users.remove(k)
        }

        users.forEach { (_, v) -> addChipToGroup(v) }
    }

    /**
     * Метод отрисовки  автарки элемента Chip.
     */
    private fun drawAvatarForChip(initials: String): Drawable {

        val paint = Paint().apply {
            isAntiAlias = true
            this.textSize = 18f
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
        }

        val textBounds = Rect()
        paint.getTextBounds(initials, 0, initials.length, textBounds)
        val backgroundBounds = RectF()
        backgroundBounds.set(0f, 0f, 40f, 40f)
        val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()

        val bitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        paint.color = getColorFromInitials(initials)
        canvas.drawCircle(20f, 20f, 20f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        paint.color = Color.WHITE
        canvas.drawText(initials, backgroundBounds.centerX(), textBottom, paint)
        return bitmap.toDrawable(resources)
    }

    /**
     * Метод выбора цвета иконки с инициалами.
     */
    private fun getColorFromInitials(initials: String): Int {
        return when (initials.hashCode() % 10) {
            0 -> resources.getColor(R.color.color_avatar1, theme)
            1 -> resources.getColor(R.color.color_avatar2, theme)
            2 -> resources.getColor(R.color.color_avatar3, theme)
            3 -> resources.getColor(R.color.color_avatar4, theme)
            4 -> resources.getColor(R.color.color_avatar5, theme)
            5 -> resources.getColor(R.color.color_avatar6, theme)
            6 -> resources.getColor(R.color.color_avatar7, theme)
            7 -> resources.getColor(R.color.color_avatar8, theme)
            8 -> resources.getColor(R.color.color_avatar9, theme)
            else -> resources.getColor(R.color.color_avatar10, theme)
        }
    }
}
