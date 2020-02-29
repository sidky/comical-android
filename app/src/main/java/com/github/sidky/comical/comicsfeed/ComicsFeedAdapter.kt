package com.github.sidky.comical.comicsfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.sidky.comical.R
import com.github.sidky.comical.data.model.FeedItem
import com.github.sidky.comical.data.model.firestore.Entry
import com.github.sidky.comical.databinding.ItemComicsFeedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

interface ComicsFeedClickListener {
    fun itemClicked(id: String)
}

class ComicsFeedViewHolder(private val binding: ItemComicsFeedBinding, private val listener: ComicsFeedClickListener) : RecyclerView.ViewHolder(binding.root) {
    fun bind(entry: FeedItem) {
        binding.id = entry.id
        binding.title = entry.title
        binding.description = entry.description
        binding.src = entry.images[0]
        binding.lastUpdated = entry.published
        binding.clickListener = listener
        binding.executePendingBindings()
    }
}

class ComicsFeedAdapter(val lifecycle: Lifecycle, val listener: ComicsFeedClickListener): RecyclerView.Adapter<ComicsFeedViewHolder>() {
    private val items = mutableListOf<FeedItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicsFeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemComicsFeedBinding>(inflater, R.layout.item_comics_feed, parent, false)
        return ComicsFeedViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ComicsFeedViewHolder, position: Int) {
        holder.bind(items[position])
    }

    suspend fun updateItems(newItems: List<FeedItem>) {
        val callback = FeedItemDiffCallback(items, newItems)
        val result = DiffUtil.calculateDiff(callback)

        CoroutineScope(Dispatchers.Main).launch {
            items.clear()
            items.addAll(newItems)
            result.dispatchUpdatesTo(this@ComicsFeedAdapter)
        }
    }

    class FeedItemDiffCallback(private val oldList: List<FeedItem>, private val newList: List<FeedItem>): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].title == newList[newItemPosition].title &&
                    compareImageList(oldList[oldItemPosition].images, newList[newItemPosition].images) &&
                    oldList[oldItemPosition].published == newList[newItemPosition].published


        private fun compareImageList(list1: List<String>, list2: List<String>): Boolean {
            if (list1.size != list2.size) return false

            return list1.zip(list2).all { it.first == it.second }
        }
    }
}