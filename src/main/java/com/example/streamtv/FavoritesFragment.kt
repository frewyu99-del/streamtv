package com.example.streamtv

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var grid: RecyclerView
    private lateinit var empty: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        grid = view.findViewById(R.id.grid)
        empty = view.findViewById(R.id.empty)
        val dm = resources.displayMetrics
        val spans = (((dm.widthPixels / dm.density) - 72) / 192).toInt().coerceAtLeast(1)
        grid.layoutManager = GridLayoutManager(requireContext(), spans)
        refresh()
    }

    private fun refresh() {
        val list = Store.favorites()
        empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        grid.adapter = CardsAdapter(list, onFavChanged = { grid.post { refresh() } }) {
            play(requireContext(), it)
        }
    }
}
