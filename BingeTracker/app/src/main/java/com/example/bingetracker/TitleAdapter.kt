package com.example.bingetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TitleAdapter(
    private var items: List<Title>,
    private val onItemClick: (Title) -> Unit,
    private val onItemLongClick: (Title) -> Unit
) : RecyclerView.Adapter<TitleAdapter.TitleViewHolder>() {

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textTypeStatus: TextView = itemView.findViewById(R.id.textTypeStatus)

        val textRating: TextView = itemView.findViewById(R.id.textRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_title, parent, false)
        return TitleViewHolder(view)
    }

    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        val title = items[position]
        holder.textName.text = title.name
        holder.textTypeStatus.text = "${title.type} • ${title.status}"

        val rating = title.rating
        if (rating != null) {
            holder.textRating.visibility = View.VISIBLE
            holder.textRating.text = "⭐ $rating/10"
        } else {
            holder.textRating.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(title)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(title)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<Title>) {
        items = newItems
        notifyDataSetChanged()
    }
}
