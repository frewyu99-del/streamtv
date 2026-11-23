package com.example.streamtv

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var list: RecyclerView
    private lateinit var progress: View
    private lateinit var errorBox: View
    private lateinit var errorText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list = view.findViewById(R.id.list)
        progress = view.findViewById(R.id.progress)
        errorBox = view.findViewById(R.id.errorBox)
        errorText = view.findViewById(R.id.errorText)
        list.layoutManager = LinearLayoutManager(requireContext())
        view.findViewById<View>(R.id.retry).setOnClickListener { load() }
        load()
    }

    private fun load() {
        progress.visibility = View.VISIBLE
        errorBox.visibility = View.GONE
        Repository.loadHome { result ->
            if (!isAdded || view == null) return@loadHome
            progress.visibility = View.GONE
            result.onSuccess { rows ->
                list.adapter = RowsAdapter(rows) { play(requireContext(), it) }
            }.onFailure { e ->
                errorText.text = getString(R.string.load_failed, e.javaClass.simpleName)
                errorBox.visibility = View.VISIBLE
                errorBox.findViewById<View>(R.id.retry).requestFocus()
            }
        }
    }
}
