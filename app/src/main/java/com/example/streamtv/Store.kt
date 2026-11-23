package com.example.streamtv

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/** Локальное хранилище: избранное, адрес каталога, последний просмотр. */
object Store {
    private lateinit var sp: SharedPreferences

    fun init(c: Context) {
        sp = c.getSharedPreferences("streamtv", Context.MODE_PRIVATE)
    }

    var catalogUrl: String
        get() = sp.getString("catalog", "") ?: ""
        set(v) { sp.edit().putString("catalog", v).apply() }

    var lastPlayed: String
        get() = sp.getString("last", "") ?: ""
        set(v) { sp.edit().putString("last", v).apply() }

    fun favorites(): List<Item> {
        val out = ArrayList<Item>()
        try {
            val a = JSONArray(sp.getString("fav", "[]") ?: "[]")
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                out.add(
                    Item(
                        o.getString("id"),
                        o.getString("title"),
                        o.optString("poster").ifBlank { null },
                        o.getString("stream")
                    )
                )
            }
        } catch (e: Exception) {
        }
        return out
    }

    private fun save(list: List<Item>) {
        val a = JSONArray()
        list.forEach {
            a.put(
                JSONObject().put("id", it.id).put("title", it.title)
                    .put("poster", it.poster ?: "").put("stream", it.stream)
            )
        }
        sp.edit().putString("fav", a.toString()).apply()
    }

    fun isFav(id: String): Boolean = favorites().any { it.id == id }

    /** true = добавлено, false = удалено */
    fun toggle(item: Item): Boolean {
        val l = favorites().toMutableList()
        val idx = l.indexOfFirst { it.id == item.id }
        return if (idx >= 0) {
            l.removeAt(idx); save(l); false
        } else {
            l.add(item); save(l); true
        }
    }

    fun clearFavorites() {
        sp.edit().remove("fav").apply()
    }
}
