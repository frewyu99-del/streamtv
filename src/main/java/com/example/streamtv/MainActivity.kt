package com.example.streamtv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private class Tab(val icon: Int, val title: String, val factory: () -> Fragment)

    private val tabs = listOf(
        Tab(R.drawable.ic_home, "Главная") { HomeFragment() },
        Tab(R.drawable.ic_search, "Поиск") { SearchFragment() },
        Tab(R.drawable.ic_bookmark, "Избранное") { FavoritesFragment() },
        Tab(R.drawable.ic_person, "Профиль") { ProfileFragment() },
        Tab(R.drawable.ic_settings, "Настройки") { SettingsFragment() }
    )
    private val buttons = ArrayList<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val nav = findViewById<LinearLayout>(R.id.nav)

        tabs.forEachIndexed { i, t ->
            val b = ImageView(this).apply {
                setImageResource(t.icon)
                contentDescription = t.title
                val p = dp(14)
                setPadding(p, p, p, p)
                isFocusable = true
                isClickable = true
                setBackgroundResource(R.drawable.bg_focus)
                layoutParams = LinearLayout.LayoutParams(dp(56), dp(56)).apply {
                    topMargin = dp(12)
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                setOnClickListener { show(i) }
            }
            buttons.add(b)
            nav.addView(b)
        }
        if (savedInstanceState == null) show(0)
    }

    private fun show(index: Int) {
        buttons.forEachIndexed { i, b -> b.isSelected = (i == index) }
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, tabs[index].factory())
            .commit()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}

fun play(ctx: Context, item: Item) {
    Store.lastPlayed = item.title
    ctx.startActivity(
        Intent(ctx, PlayerActivity::class.java)
            .putExtra("url", item.stream)
            .putExtra("title", item.title)
    )
}
