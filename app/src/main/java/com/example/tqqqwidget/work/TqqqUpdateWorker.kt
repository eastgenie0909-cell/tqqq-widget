package com.example.tqqqwidget.work

import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.*
import com.example.tqqqwidget.data.DataRepository
import com.example.tqqqwidget.data.WidgetState
import com.example.tqqqwidget.data.WidgetStateRepo
import com.example.tqqqwidget.ui.ChartRenderer
import com.example.tqqqwidget.widget.TqqqWidget
import java.util.*
import java.util.concurrent.TimeUnit

class TqqqUpdateWorker(ctx: android.content.Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        try {
            val candles = DataRepository.fetchDaily(250)
            val lastClose = candles.last().close
            val dma200 = candles.takeLast(200).map { it.close }.average()
            val envTop = dma200 * 1.05

            val bmp = ChartRenderer.render(candles, dma200, envTop)
            val state = WidgetState(lastClose = lastClose, dma200 = dma200, envTop = envTop, updateTime = Date().toString())
            WidgetStateRepo.write(applicationContext, state, bmp)

            Log.i("TqqqUpdateWorker", "Updated close=$lastClose dma200=$dma200 envTop=$envTop")

            val glanceManager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = glanceManager.getGlanceIds(TqqqWidget::class.java)
            if (glanceIds.isNotEmpty()) {
                glanceIds.forEach { id -> TqqqWidget().update(applicationContext, id) }
            } else {
                Log.i("TqqqUpdateWorker", "No glanceIds found")
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("TqqqUpdateWorker", "Worker failed", e)
            return Result.retry()
        }
    }

    companion object {
        fun schedule(context: android.content.Context) {
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val req = PeriodicWorkRequestBuilder<TqqqUpdateWorker>(2, TimeUnit.HOURS).setConstraints(constraints).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork("tqqq_update", ExistingPeriodicWorkPolicy.UPDATE, req)
        }

        fun enqueueNow(context: android.content.Context) {
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val oneReq = OneTimeWorkRequestBuilder<TqqqUpdateWorker>().setConstraints(constraints).build()
            WorkManager.getInstance(context).enqueue(oneReq)
        }
    }
}
