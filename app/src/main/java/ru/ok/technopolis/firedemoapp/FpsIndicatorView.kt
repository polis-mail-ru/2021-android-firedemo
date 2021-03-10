package ru.ok.technopolis.firedemoapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import java.util.*

class FpsIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Handler.Callback {

    companion object {
        private const val MSG_NEXT_FRAME = 1
    }

    private var frameCount = 0
    private var startTs: Long
    private var fpsText: String? = null
    private var fpsDigits = 0
    private val bgPaint = Paint()
    private val textPaint = TextPaint()
    private val textBounds = Rect()
    private val messageHandler = Handler(this)

    init {
        bgPaint.color = Color.BLACK
        textPaint.textSize = 20 * context.resources.displayMetrics.density
        textPaint.color = Color.YELLOW
        textPaint.getTextBounds("WWWW", 0, 3, textBounds)
        startTs = SystemClock.uptimeMillis()
        messageHandler.sendMessage(Message.obtain(messageHandler, MSG_NEXT_FRAME))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val textLeft = when {
            fpsDigits >= 4 -> 0
            fpsDigits == 3 -> width / 4
            fpsDigits == 2 -> width / 2
            else -> width * 3 / 4
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        if (fpsText != null) {
            canvas.drawText(fpsText!!, textLeft.toFloat(), height.toFloat(), textPaint)
        }
        frameCount++
        invalidate()
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == MSG_NEXT_FRAME) {
            val ts = SystemClock.uptimeMillis()
            if (ts - startTs > 1000) {
                fpsText = if (frameCount > 10) {
                    (1000 * frameCount / (ts - startTs).toInt()).toString()
                } else {
                    val fps = 1000.0 * frameCount / (ts - startTs)
                    String.format(Locale.ENGLISH, "%.2f", fps)
                }
                fpsDigits = fpsText!!.length
                startTs = ts
                frameCount = 0
                invalidate()
            }
            messageHandler.sendMessageDelayed(Message.obtain(messageHandler, MSG_NEXT_FRAME), 100)
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            resolveSize(textBounds.width(), widthMeasureSpec),
            resolveSize(textBounds.height(), heightMeasureSpec)
        )
    }

}