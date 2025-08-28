package com.example.tqqqwidget.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.tqqqwidget.data.Candle

object ChartRenderer {
    fun render(candles: List<Candle>, dma200: Double, envTop: Double): Bitmap {
        val w = 600
        val h = 300
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.BLACK)

        val prices = candles.map { it.close }
        val min = prices.minOrNull() ?: 0.0
        val max = prices.maxOrNull() ?: (min + 1.0)
        val scale = h / (max - min).toFloat()

        val paint = Paint().apply {
            color = Color.WHITE
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }

        val step = w.toFloat() / candles.size.toFloat()
        var prevX = 0f
        var prevY = 0f
        candles.forEachIndexed { i, candle ->
            val x = i * step
            val y = h - (candle.close - min).toFloat() * scale
            if (i == 0) { prevX = x; prevY = y } else {
                c.drawLine(prevX, prevY, x, y, paint)
                prevX = x; prevY = y
            }
        }

        val dmaY = h - (dma200 - min).toFloat() * scale
        paint.color = Color.YELLOW
        c.drawLine(0f, dmaY, w.toFloat(), dmaY, paint)

        val envY = h - (envTop - min).toFloat() * scale
        paint.color = Color.RED
        c.drawLine(0f, envY, w.toFloat(), envY, paint)

        return bmp
    }
}
