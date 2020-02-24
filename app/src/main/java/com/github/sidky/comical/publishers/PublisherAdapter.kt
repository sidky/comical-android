package com.github.sidky.comical.publishers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.github.sidky.comical.R
import com.github.sidky.comical.data.model.Publisher
import com.github.sidky.comical.databinding.ItemPublisherBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.format.DateTimeFormatter

class PublisherViewHolder(private val binding: ItemPublisherBinding) : RecyclerView.ViewHolder(binding.root) {

    private val formatter by lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }

    fun bind(info: Publisher, listener: PublisherItemSelectListener) {
        binding.id = info.id
        binding.name = info.name
        binding.description = info.description
        formatter.format(info.lastUpdated)
        binding.favicon.load(info.icon) {
            crossfade(true)
            transformations(RoundedCornersTransformation(topLeft = 10.0f, topRight = 10.0f, bottomLeft = 10.0f, bottomRight = 10.0f))
        }
        binding.handler = listener
        binding.executePendingBindings()
    }
}

interface PublisherItemSelectListener {
    fun onPublisherSelected(id: String)
}

class PublisherAdapter @ExperimentalCoroutinesApi constructor(private val presenter: PublishersPresenter): RecyclerView.Adapter<PublisherViewHolder>(), PublisherItemSelectListener {
    private var _items: List<Publisher> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublisherViewHolder {
        val binding: ItemPublisherBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_publisher, parent,false)
        val holder = PublisherViewHolder(binding = binding)
        return holder
    }

    override fun getItemCount(): Int = _items.size

    override fun onBindViewHolder(holder: PublisherViewHolder, position: Int) = holder.bind(_items[position], this)

    public fun update(list: List<Publisher>) {
        _items = list
        notifyDataSetChanged()
    }

    @ExperimentalCoroutinesApi
    override fun onPublisherSelected(id: String) {
        presenter.publisherSelected(id)
    }
}