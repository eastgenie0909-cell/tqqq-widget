package com.example.tqqqwidget.data

import java.util.*

data class Candle(val timeMillis: Long, val close: Double)

object DataRepository {
    fun fetchDaily(lookback: Int = 250): List<Candle> {
        val now = System.currentTimeMillis()
        val day = 24L * 60 * 60 * 1000
        val base = 70.0
        val list = mutableListOf<Candle>()
        var last = base
        for (i in (lookback - 1) downTo 0) {
            val t = now - i * day
            val change = (Math.random() - 0.5) * 2.0
            val close = (last + change).coerceAtLeast(1.0)
            list.add(Candle(t, close))
            last = close
        }
        return list
    }
}
