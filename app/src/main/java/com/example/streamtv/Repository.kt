package com.example.streamtv

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * Загружает каталог. Формат JSON:
 * {"rows":[{"title":"Новинки","items":[{"id":"1","title":"Фильм","poster":"https://…jpg","stream":"https://…m3u8"}]}]}
 */
object Repository {
    private val io = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())

    @Volatile
    var cache: List<Row> = emptyList()

    fun loadHome(cb: (Result<List<Row>>) -> Unit) {
        io.execute {
            val result = runCatching { fetch() }
            result.onSuccess { cache = it }
            main.post { cb(result) }
        }
    }

    private fun fetch(): List<Row> {
        if (BuildConfig.API_URL.isBlank()) return demo()
        val c = URL(BuildConfig.API_URL).openConnection() as HttpURLConnection
        c.connectTimeout = 10_000
        c.readTimeout = 15_000
        try {
            if (c.responseCode != 200) throw IOException("HTTP " + c.responseCode)
            val text = c.inputStream.bufferedReader().use { it.readText() }
            return parse(text)
        } finally {
            c.disconnect()
        }
    }

    private fun parse(text: String): List<Row> {
        val rows = JSONObject(text).getJSONArray("rows")
        val out = ArrayList<Row>()
        for (i in 0 until rows.length()) {
            val r = rows.getJSONObject(i)
            val arr = r.getJSONArray("items")
            val items = ArrayList<Item>()
            for (j in 0 until arr.length()) {
                val o = arr.getJSONObject(j)
                items.add(
                    Item(
                        o.getString("id"),
                        o.getString("title"),
                        o.optString("poster").ifBlank { null },
                        o.getString("stream")
                    )
                )
            }
            out.add(Row(r.getString("title"), items))
        }
        return out
    }

    private fun demo(): List<Row> {
        val hls = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
        val mp4 = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        fun items(prefix: String) = (1..8).map {
            Item("$prefix$it", "$prefix $it", null, if (it % 2 == 0) hls else mp4)
        }
        return listOf(
            Row("Новинки", items("Новинка")),
            Row("Популярное", items("Популярное")),
            Row("Рекомендуем", items("Подборка"))
        )
    }
}
