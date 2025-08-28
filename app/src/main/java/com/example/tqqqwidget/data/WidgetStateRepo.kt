package com.example.tqqqwidget.data

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import java.io.File
import java.io.Serializable

data class WidgetState(
    val lastClose: Double = 0.0,
    val dma200: Double = 0.0,
    val envTop: Double = 0.0,
    val updateTime: String = "",
    val chartPathString: String? = null
) : Serializable

object WidgetStateRepo {
    private const val STATE_FILE = "widget_state.json"
    private const val CHART_FILE = "tqqq_chart.png"

    fun write(context: Context, state: WidgetState, chartBitmap: Bitmap) {
        val gson = Gson()
        val cacheDir = context.externalCacheDir ?: context.cacheDir

        val stateFile = File(cacheDir, STATE_FILE)
        stateFile.writeText(gson.toJson(state.copy(chartPathString = CHART_FILE)))

        val chartFile = File(cacheDir, CHART_FILE)
        chartFile.outputStream().use { out ->
            chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    fun read(context: Context): WidgetState {
        val gson = Gson()
        val cacheDir = context.externalCacheDir ?: context.cacheDir
        val stateFile = File(cacheDir, STATE_FILE)
        val chartFile = File(cacheDir, CHART_FILE)
        return if (stateFile.exists()) {
            val s = gson.fromJson(stateFile.readText(), WidgetState::class.java)
            s.copy(chartPathString = if (chartFile.exists()) chartFile.absolutePath else null)
        } else {
            WidgetState()
        }
    }

    fun getChartFile(context: Context): File? {
        val cacheDir = context.externalCacheDir ?: context.cacheDir
        val f = File(cacheDir, CHART_FILE)
        return if (f.exists()) f else null
    }
}
