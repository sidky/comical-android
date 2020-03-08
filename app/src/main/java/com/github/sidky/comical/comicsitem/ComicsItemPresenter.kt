package com.github.sidky.comical.comicsitem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sidky.comical.arch.ArchPresenter
import com.github.sidky.comical.arch.BaseInteraction
import com.github.sidky.comical.data.ComicsRepository
import com.github.sidky.comical.data.model.FeedItem
import com.github.sidky.comical.data.model.firestore.ComicsInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ComicsItemInteractions: BaseInteraction {
    data class Item(val image: String): ComicsItemInteractions()
}

class ComicsItemPresenter @Inject constructor(private val parameter: ComicsItemFragmentArgs,
                                              private val repository: ComicsRepository): ArchPresenter<ComicsItemInteractions>, ViewModel() {

    private var comicsInfo: FeedItem? = null

    init {
        viewModelScope.launch {
            val info = repository.comics(parameter.feedId, parameter.comicsId)
            comicsInfo = info
            val state = ComicsItemInteractions.Item(info.images[0])
            dispatchState(state)
        }
    }

    private val interactionsChannel = Channel<ComicsItemInteractions>()

    override fun interactions(): Channel<ComicsItemInteractions> = interactionsChannel

    private fun dispatchState(interaction: ComicsItemInteractions) {
        viewModelScope.launch(Dispatchers.Main) {
            interactionsChannel.send(interaction)
        }
    }
}