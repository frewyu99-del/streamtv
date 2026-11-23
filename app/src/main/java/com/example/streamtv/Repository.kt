package com.example.streamtv

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * Загружает каталог по адресу из «Настроек» (или API_URL).
 * Поддерживаются два формата:
 *  1) JSON: {"rows":[{"title":"..","items":[{"id":"1","title":"..","poster":"https://..","stream":"https://.."}]}]}
 *  2) M3U-плейлист (#EXTM3U, tvg-logo, group-title)
 * Если адрес не задан — показывается демо-каталог.
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
        val url = Store.catalogUrl.ifBlank { BuildConfig.API_URL }
        if (url.isBlank()) return demo()
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 10_000
        c.readTimeout = 20_000
        try {
            if (c.responseCode != 200) throw IOException("HTTP " + c.responseCode)
            val text = c.inputStream.bufferedReader().use { it.readText() }
            return if (text.trimStart().startsWith("#EXTM3U")) parseM3u(text) else parseJson(text)
        } finally {
            c.disconnect()
        }
    }

    private fun parseJson(text: String): List<Row> {
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

    private fun parseM3u(text: String): List<Row> {
        val groups = LinkedHashMap<String, MutableList<Item>>()
        var title = ""
        var logo: String? = null
        var group = "Каналы"
        val logoRe = Regex("tvg-logo=\"([^\"]*)\"")
        val groupRe = Regex("group-title=\"([^\"]*)\"")
        for (raw in text.lineSequence()) {
            val line = raw.trim()
            if (line.startsWith("#EXTINF")) {
                title = line.substringAfterLast(',').trim()
                logo = logoRe.find(line)?.groupValues?.get(1)?.ifBlank { null }
                group = groupRe.find(line)?.groupValues?.get(1)?.ifBlank { null } ?: "Каналы"
            } else if (line.isNotEmpty() && !line.startsWith("#")) {
                groups.getOrPut(group) { ArrayList() }
                    .add(Item(line.hashCode().toString(), title.ifBlank { line }, logo, line))
                title = ""
                logo = null
            }
        }
        return groups.map { Row(it.key, it.value) }
    }

    /** Открытые демо-видео (Blender Foundation и тестовые ролики Google). */
    private fun demo(): List<Row> {
        val base = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/"
        fun v(title: String, name: String) =
            Item(name, title, base + "images/" + name + ".jpg", base + name + ".mp4")
        return listOf(
            Row(
                "Мультфильмы Blender",
                listOf(
                    v("Big Buck Bunny", "BigBuckBunny"),
                    v("Elephants Dream", "ElephantsDream"),
                    v("Sintel", "Sintel"),
                    v("Tears of Steel", "TearsOfSteel")
                )
            ),
            Row(
                "Короткие ролики",
                listOf(
                    v("For Bigger Blazes", "ForBiggerBlazes"),
                    v("For Bigger Escapes", "ForBiggerEscapes"),
                    v("For Bigger Fun", "ForBiggerFun"),
                    v("For Bigger Joyrides", "ForBiggerJoyrides"),
                    v("For Bigger Meltdowns", "ForBiggerMeltdowns")
                )
            ),
            Row(
                "Авто",
                listOf(
                    v("Subaru Outback", "SubaruOutbackOnStreetAndDirt"),
                    v("Volkswagen GTI Review", "VolkswagenGTIReview"),
                    v("We Are Going On Bullrun", "WeAreGoingOnBullrun"),
                    v("What Car Can You Get For A Grand", "WhatCarCanYouGetForAGrand")
                )
            )
        )
    }
}
