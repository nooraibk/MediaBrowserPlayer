package com.example.mediabrowserplayer.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.data.Track

class TracksRecyclerView(private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<TracksRecyclerView.ExampleViewHolder>() {

    private var data = ArrayList<Track>()

    interface ItemClickListener {
        fun onItemClick(trackIndex: Int)
    }

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtView: TextView = itemView.findViewById(R.id.trackTitle)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExampleViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.recycler_item_view, parent, false)

        return ExampleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        holder.txtView.text = data[position].title

        holder.itemView.setOnClickListener {
            listener.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTracksData(tracksList: List<Track>) {
        data.clear()
        data.addAll(tracksList)
        notifyDataSetChanged()
    }
}