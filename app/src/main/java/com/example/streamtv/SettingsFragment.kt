package com.example.streamtv

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val url = view.findViewById<EditText>(R.id.url)
        val status = view.findViewById<TextView>(R.id.status)
        url.setText(Store.catalogUrl)

        fun applyUrl() {
            Store.catalogUrl = url.text.toString().trim()
            Repository.cache = emptyList()
            status.text = if (Store.catalogUrl.isBlank())
                "Используется демо-каталог. Откройте «Главная»."
            else
                "Сохранено. Откройте «Главная», чтобы загрузить каталог."
        }
        view.findViewById<View>(R.id.save).setOnClickListener { applyUrl() }
        view.findViewById<View>(R.id.reset).setOnClickListener {
            url.setText("")
            applyUrl()
        }
        view.findViewById<View>(R.id.clearFav).setOnClickListener {
            Store.clearFavorites()
            status.text = "Избранное очищено."
        }
    }
}
