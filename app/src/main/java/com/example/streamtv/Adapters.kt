package com.example.streamtv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RowsAdapter(
    private val rows: List<Row>,
    private val onClick: (Item) -> Unit
) : RecyclerView.Adapter<RowsAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.rowTitle)
        val list: RecyclerView = v.findViewById(R.id.rowList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        val vh = VH(v)
        vh.list.layoutManager =
            LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.title.text = rows[position].title
        holder.list.adapter = CardsAdapter(rows[position].items, onClick)
    }

    override fun getItemCount() = rows.size
}

class CardsAdapter(
    private val items: List<Item>,
    private val onClick: (Item) -> Unit
) : RecyclerView.Adapter<CardsAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val poster: ImageView = v.findViewById(R.id.poster)
        val title: TextView = v.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        if (item.poster != null) {
            Glide.with(holder.poster).load(item.poster).centerCrop().into(holder.poster)
        } else {
            Glide.with(holder.poster).clear(holder.poster)
            holder.poster.setImageDrawable(null)
        }
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
