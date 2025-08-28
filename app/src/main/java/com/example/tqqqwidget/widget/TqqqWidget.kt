package com.example.tqqqwidget.widget

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.dp
import com.example.tqqqwidget.data.WidgetStateRepo

class TqqqWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        val ctx = LocalContext.current
        val state = WidgetStateRepo.read(ctx)

        Column(modifier = GlanceModifier.fillMaxSize().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("TQQQ·SPLG 풀매도, SGOV 풀매수", style = TextStyle(color = ColorProvider(android.graphics.Color.RED)))
            Spacer(GlanceModifier.height(6.dp))

            val chartFile = WidgetStateRepo.getChartFile(ctx)
            val imgProv = try {
                chartFile?.let {
                    val uri: Uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", it)
                    ImageProvider(uri)
                }
            } catch (e: Exception) {
                null
            }

            if (imgProv != null) {
                Image(provider = imgProv, contentDescription = "chart", modifier = GlanceModifier.width(220.dp).height(100.dp))
            } else {
                Text("차트 로딩 중...", style = TextStyle(color = ColorProvider(android.graphics.Color.WHITE)))
            }

            Spacer(GlanceModifier.height(6.dp))
            Column {
                Text("TQQQ: ${'$'}{state.lastClose}")
                Text("200 MA: ${'$'}{state.dma200}", style = TextStyle(color = ColorProvider(android.graphics.Color.YELLOW)))
                Text("ENV상단: ${'$'}{state.envTop}", style = TextStyle(color = ColorProvider(android.graphics.Color.RED)))
            }

            Spacer(GlanceModifier.height(6.dp))
            Text("업데이트: ${'$'}{state.updateTime}")
        }
    }
}

class TqqqWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TqqqWidget()
}
