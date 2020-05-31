package ru.skillbranch.devintensive.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.setBackgroundDrawable
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.archive.ArchiveActivity
import ru.skillbranch.devintensive.ui.group.GroupActivity
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.MainViewModel

/**
 * Класс, отвечающий за отрисовку главной активности.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: MainViewModel

    /**
     * Реализация базового метода ЖЦ для создания активности.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        initToolbar()
        initViews()
    }

    /**
     * Инициализация ViewModel.
     */
    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getChatData().observe(this, Observer { chatAdapter.updateData(it) })
    }

    /**
     * Метод для инициализации графических элементов.
     */
    private fun initViews() {

        // адаптер для списка чатов.
        chatAdapter = ChatAdapter {
            if (it.chatType == ChatType.ARCHIVE) {
                val intent = Intent(this, ArchiveActivity::class.java)
                startActivity(intent)
            } else {
                Snackbar.make(rv_chat_list, "Click on ${it.title}", Snackbar.LENGTH_LONG)
                        .setTextColor(Utils.getCurrntModeColor(this, R.attr.colorSnackBarText))
                        .setBackgroundDrawable(R.drawable.bg_snackbar)
                        .show()
            }
        }

        // разделитель элементов списка
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_chat_list, theme))

        // обработчик свайпа элемента
        val touchCallback = ChatItemTouchHelperCallback(chatAdapter, false) {
            val id = it.id
            viewModel.addToArchive(it.id)
            val layoutManager = rv_chat_list.layoutManager as LinearLayoutManager
            if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                rv_chat_list.scrollToPosition(0)
            }

            Snackbar.make(rv_chat_list,"Вы точно хотите добавить ${it.title} в архив?", Snackbar.LENGTH_LONG)
                    .setTextColor(Utils.getCurrntModeColor(this, R.attr.colorSnackBarText))
                    .setBackgroundTint(resources.getColor(R.color.color_primary, theme))
                    .setAction("ОТМЕНА") { viewModel.restoreFromArchive(id) }
                    .show()
        }

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_chat_list)

        // привязка адаптера для списка чатов
        with(rv_chat_list) {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(divider)
        }

        // обработчик нажатия на кнопку добавления группового чата
        fab.setOnClickListener {
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Инициализация Toolbar.
     */
    private fun initToolbar() {
        setSupportActionBar(toolbar)
        viewModel.getChatData().observe(this, Observer { chatAdapter.updateData(it) })
    }

    /**
     * Метод создания контекстного меню для реализации поиска.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearchQuery(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }
}
