package com.github.sidky.comical.comicsfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.sidky.comical.R
import com.github.sidky.comical.data.model.FeedItem
import com.github.sidky.comical.data.model.firestore.Entry
import com.github.sidky.comical.databinding.ItemComicsFeedBinding

class ComicsFeedViewHolder(private val binding: ItemComicsFeedBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(entry: FeedItem) {
        binding.title = entry.title
        binding.description = entry.description
        binding.src = entry.images[0]
        binding.lastUpdated = entry.published
        binding.executePendingBindings()
    }
}

class ComicsFeedAdapter: RecyclerView.Adapter<ComicsFeedViewHolder>() {
    private val items = mutableListOf<FeedItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicsFeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemComicsFeedBinding>(inflater, R.layout.item_comics_feed, parent, false)
        return ComicsFeedViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ComicsFeedViewHolder, position: Int) {
        holder.bind(items[position])
    }

}