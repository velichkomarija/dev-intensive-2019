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

class AvatarImageViewMask @JvmOverloads constructor(
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

    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()

    private lateinit var resultBm: Bitmap
    private lateinit var maskBm: Bitmap
    private lateinit var srcBm: Bitmap

    init {
        if (attributeSet != null) {
            val typeAttributeSet = context.obtainStyledAttributes(attributeSet, R.styleable.AvatarImageViewMask)
            borderWidth = typeAttributeSet.getDimension(R.styleable.AvatarImageViewMask_aivm_borderWidth,
                    context.dpToPx(DEFAULT_BORDER_WIDTH))
            borderColor = typeAttributeSet.getColor(R.styleable.AvatarImageViewMask_aivm_borderColor,
                    DEFAULT_BORDER_COLOR)
            initials = typeAttributeSet.getString(R.styleable.AvatarImageViewMask_aivm_initials)
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

        prepareBitmaps(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(resultBm, viewRect, viewRect, null)


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
        with(maskPaint) {
            color = Color.RED
            style = Paint.Style.FILL
        }

        with(borderPaint){
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
    }

    private fun prepareBitmaps(w : Int, h: Int) {
        maskBm = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        resultBm = maskBm.copy(Bitmap.Config.ARGB_8888, true)

        val maskCanvas = Canvas(maskBm)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        maskCanvas.drawOval(viewRect.toRectF(), maskPaint)

        srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)

        val resultCanvas = Canvas(resultBm)

        resultCanvas.drawBitmap(maskBm, viewRect, viewRect, null)
        resultCanvas.drawBitmap(srcBm, viewRect, viewRect, maskPaint)

    }
}