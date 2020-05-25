package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx

class AvatarImageViewShader @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttributeSet: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attributeSet, defStyleAttributeSet) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_SIZE: Int = 40
    }

    @Px
    var borderWidth: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)
    @ColorInt
    private var borderColor: Int = Color.WHITE
    private var initials: String = "??"

    private val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()


    init {
        if (attributeSet != null) {
            val typeAttributeSet = context.obtainStyledAttributes(attributeSet, R.styleable.AvatarImageViewShader)
            borderWidth = typeAttributeSet.getDimension(R.styleable.AvatarImageViewShader_aivs_borderWidth,
                    context.dpToPx(DEFAULT_BORDER_WIDTH))
            borderColor = typeAttributeSet.getColor(R.styleable.AvatarImageViewShader_aivs_borderColor,
                    DEFAULT_BORDER_COLOR)
            initials = typeAttributeSet.getString(R.styleable.AvatarImageViewShader_aivs_initials)
                    ?: "??"
            typeAttributeSet.recycle()
        }

        scaleType = ScaleType.CENTER_CROP
        setup()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int,
                           heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)
    }

    override fun onSizeChanged(w: Int,
                               h: Int,
                               oldw: Int,
                               oldh: Int) {

        super.onSizeChanged(w, h, oldw, oldh)

        if (w == 0) {
            return
        }

        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h

        }

        prepareShader(w, h)
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawOval(viewRect.toRectF(), avatarPaint)
        val half = (borderWidth / 2).toInt()
        viewRect.inset(half, half)
        canvas.drawOval( viewRect.toRectF(), borderPaint)
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.dpToPx(DEFAULT_SIZE).toInt()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

    private fun setup() {
        with(borderPaint){
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
    }

    private fun prepareShader(w : Int, h: Int) {
        val srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }
}