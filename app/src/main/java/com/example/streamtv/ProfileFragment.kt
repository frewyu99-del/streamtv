package com.example.streamtv

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_placeholder) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tv = view.findViewById<TextView>(R.id.title)
        tv.gravity = Gravity.CENTER
        tv.textSize = 22f
        val last = Store.lastPlayed.ifBlank { "—" }
        tv.text = "Гость\n\nВ избранном: " + Store.favorites().size +
            "\nПоследний просмотр: " + last +
            "\n\nStreamTV " + BuildConfig.VERSION_NAME
    }
}
