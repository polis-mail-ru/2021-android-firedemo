package ru.ok.technopolis.firedemoapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.max
import kotlin.math.min

class FireView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val firePalette = intArrayOf(
            -0xf8f8f9,
            -0xe0f8f9,
            -0xd0f0f9,
            -0xb8f0f9,
            -0xa8e8f9,
            -0x98e0f9,
            -0x88e0f9,
            -0x70d8f9,
            -0x60d0f9,
            -0x50c0f9,
            -0x40b8f9,
            -0x38b8f9,
            -0x20b0f9,
            -0x20a8f9,
            -0x20a8f9,
            -0x28a0f9,
            -0x28a0f9,
            -0x2898f1,
            -0x3090f1,
            -0x3088f1,
            -0x3080f1,
            -0x3078e9,
            -0x3878e9,
            -0x3870e9,
            -0x3868e1,
            -0x4060e1,
            -0x4060e1,
            -0x4058d9,
            -0x4058d9,
            -0x4050d1,
            -0x4850d1,
            -0x4848d1,
            -0x4848c9,
            -0x303091,
            -0x202061,
            -0x101039,
            -0x1
        )
    }

    private lateinit var firePixels: IntArray
    private var fireWidth = 0
    private var fireHeight = 0
    private var bitmapPixels = IntArray(0)
    private var minHotY = -1
    private val paint = Paint()
    private val random = Random()
    private lateinit var bitmap: Bitmap

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val aspectRatio = w.toFloat() / h
        fireWidth = 150
        fireHeight = (fireWidth / aspectRatio).toInt()
        firePixels = IntArray(fireWidth * fireHeight)
        for (x in 0 until fireWidth) {
            firePixels[(fireHeight - 1) * fireWidth + x] = firePalette.size - 1
        }
        bitmap = Bitmap.createBitmap(fireWidth, fireHeight, Bitmap.Config.RGB_565)
    }

    override fun onDraw(canvas: Canvas) {
        spreadFire()
        drawFire(canvas)
        invalidate()
    }

    private fun drawFire(canvas: Canvas) {
        val pixelCount = fireWidth * fireHeight
        if (bitmapPixels.size < pixelCount) {
            bitmapPixels = IntArray(pixelCount)
        }
        val startY = if (minHotY < 0) 0 else minHotY
        for (y in startY until fireHeight) {
            for (x in 0 until fireWidth) {
                var temperature = firePixels[x + y * fireWidth]
                if (temperature < 0) {
                    temperature = 0
                }
                if (temperature >= firePalette.size) {
                    temperature = firePalette.size - 1
                }
                val color = firePalette[temperature]
                bitmapPixels[fireWidth * y + x] = color
            }
        }
        bitmap.setPixels(bitmapPixels, 0, fireWidth, 0, 0, fireWidth, fireHeight)
        val scale = width.toFloat() / fireWidth
        canvas.scale(scale, scale)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun spreadFire() {
        val startY = if (minHotY < 0) 0 else max(0, minHotY - 5)
        var minHotY = -1
        for (y in startY until fireHeight - 1) {
            for (x in 0 until fireWidth) {
                val randX = random.nextInt(3)
                val randY = random.nextInt(6)
                val dstX = min(fireWidth - 1, max(0, x + randX - 1))
                val dstY = min(fireHeight - 1, y + randY)
                val deltaFire = -(randX and 1)
                val temp = max(0, firePixels[dstX + dstY * fireWidth] + deltaFire)
                firePixels[x + y * fireWidth] = temp
                if (minHotY == -1 && temp > 0) {
                    minHotY = y
                }
            }
        }
        this.minHotY = minHotY
    }
}