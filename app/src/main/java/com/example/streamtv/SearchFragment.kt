package com.example.streamtv

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var results: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val query = view.findViewById<EditText>(R.id.query)
        results = view.findViewById(R.id.results)
        val dm = resources.displayMetrics
        val spans = (((dm.widthPixels / dm.density) - 72) / 192).toInt().coerceAtLeast(1)
        results.layoutManager = GridLayoutManager(requireContext(), spans)

        query.doAfterTextChanged { refresh(it?.toString().orEmpty()) }
        if (Repository.cache.isEmpty()) {
            Repository.loadHome { if (isAdded) refresh(query.text.toString()) }
        }
        refresh("")
    }

    private fun refresh(q: String) {
        val found = Repository.cache.flatMap { it.items }.distinctBy { it.id }
            .filter { q.isBlank() || it.title.contains(q, ignoreCase = true) }
        results.adapter = CardsAdapter(found) { play(requireContext(), it) }
    }
}
