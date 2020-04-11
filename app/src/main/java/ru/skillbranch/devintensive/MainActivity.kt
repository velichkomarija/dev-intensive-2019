package ru.skillbranch.devintensive

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.models.Bender

class MainActivity : AppCompatActivity(),
        View.OnClickListener {

    lateinit var benderImage: ImageView
    lateinit var textTxt: TextView
    lateinit var messageEt: EditText
    lateinit var sendBtn: ImageView

    lateinit var benderObj: Bender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        benderImage = iv_bender
        textTxt = tv_text
        messageEt = et_message
        benderImage = iv_bender
        sendBtn = iv_send
        sendBtn.setOnClickListener(this)

        val status = savedInstanceState?.getString("STATUS") ?: Bender.Status.NORMAL.name
        val question = savedInstanceState?.getString("QUESTION") ?: Bender.Question.NAME.name
        benderObj = Bender(Bender.Status.valueOf(status), Bender.Question.valueOf(question))

        val (r, g, b) = benderObj.status.color
        benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)

        textTxt.text = benderObj.askQuestion()

        messageEt.setOnEditorActionListener(object : TextView.OnEditorActionListener
        {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean
            {
                if (p1 == EditorInfo.IME_ACTION_DONE && p0 != null)
                    changeBenderState()

                return true
            }

        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("STATUS", benderObj.status.name)
        outState.putString("QUESTION", benderObj.question.name)
    }

    @SuppressLint("DefaultLocale")
    override fun onClick(view: View?) {
      changeBenderState()
    }

    private fun changeBenderState() {
        val (phrase, color) =
                benderObj.listenAnswer(
                        messageEt.text.toString())

        val (r, g, b) = color
        benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)
        textTxt.text = phrase
    }
}
