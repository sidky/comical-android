package com.github.sidky.comical.comicsfeed

import androidx.annotation.GuardedBy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sidky.comical.arch.ArchPresenter
import com.github.sidky.comical.arch.BaseInteraction
import com.github.sidky.comical.common.FeatureScope
import com.github.sidky.comical.data.ComicsRepository
import com.github.sidky.comical.data.model.FeedItem
import com.github.sidky.comical.data.model.firestore.Entry
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

sealed class ComicsFeedInteractions: BaseInteraction {
    data class Feed(val feed: List<FeedItem>, val isLoading: Boolean, val canRefresh: Boolean): ComicsFeedInteractions()
    data class Navigate(val feedId: String, val comicsId: String): ComicsFeedInteractions()
    class Error(val message: String): ComicsFeedInteractions()
}

@ExperimentalCoroutinesApi
@FeatureScope
class ComicsFeedPresenter @Inject constructor(
    private val parameter: ComicsFeedFragmentArgs,
    private val repository: ComicsRepository
): ViewModel(), ArchPresenter<ComicsFeedInteractions>, ComicsFeedClickListener {

    @GuardedBy("this")
    private val comicsFeed = mutableListOf<FeedItem>()
    private var completeLoad: Boolean = false
    private var isLoading: Boolean = false

    private val interactionChannel = Channel<ComicsFeedInteractions>()

    override fun interactions(): Channel<ComicsFeedInteractions> = interactionChannel

    private val pageUpdate = Channel<Pair<Boolean, List<FeedItem>>>()
    private val loadMore = Channel<Unit>()

    private var pagedUpdatesJob: Job
    private var listBuilderJob: Job

    init {
        pagedUpdatesJob = registerFeedUpdate()
        registerFeedChange()
        listBuilderJob = listBuilder()
    }

    @ExperimentalCoroutinesApi
    fun registerFeedUpdate(): Job = viewModelScope.launch {
        var starting = true
        for (page in repository.pagedComicsFeed(parameter.id, PAGE_SIZE, 1, viewModelScope)) {
            pageUpdate.send(Pair(starting, page))
            starting = false
            Timber.e("WAIT")
            loadMore.receive()
            Timber.e("WAIT OVER")
        }
        synchronized(this) {
            completeLoad = true
        }
    }

    @ExperimentalCoroutinesApi
    fun registerFeedChange() {
        viewModelScope.launch {
            repository.feedUpdated(parameter.id, viewModelScope).collect {
                pagedUpdatesJob.cancel()
                pagedUpdatesJob = registerFeedUpdate()
            }
        }
    }

    private fun listBuilder() = viewModelScope.launch {
        try {
            while (isActive) {
                val update = pageUpdate.receive()
                Timber.e("LOADED: $update")

                synchronized(this) {
                    if (update.first == true) {
                        comicsFeed.clear()
                    }
                    comicsFeed.addAll(update.second)
                }

                dispatchFeedUpdate()
            }
        } catch (ex: CancellationException) {
            Timber.e("ComicsFeedPresenter channel cancelled")
        }
        Timber.e("DONE")
    }

    private fun dispatchFeedUpdate() {
        val state = synchronized(this) {
            ComicsFeedInteractions.Feed(comicsFeed.toList(), isLoading, completeLoad)
        }

        CoroutineScope(Dispatchers.Main).launch {
            interactionChannel.send(state)
        }
    }

    fun loadMore() {
        Timber.e("LOAD MORE: ${listBuilderJob.isActive}")
        viewModelScope.launch {
            val doNotLoadMore = synchronized(this) {
                completeLoad || isLoading
            }
            Timber.e("DO NOT LOAD MORE: $doNotLoadMore")
            if (!doNotLoadMore) {
                Timber.e("SEND MORE")
                loadMore.send(Unit)
            }
        }
        Timber.e("REQUESTED LOAD MORE")
    }

    companion object {
        val PAGE_SIZE = 10
    }

    override fun itemClicked(id: String) {
        CoroutineScope(Dispatchers.Main).launch {
            interactionChannel.send(ComicsFeedInteractions.Navigate(parameter.id, id))
        }
    }
}