package com.github.sidky.comical.publishers

import androidx.lifecycle.*
import com.github.sidky.comical.arch.ArchPresenter
import com.github.sidky.comical.arch.BaseInteraction
import com.github.sidky.comical.common.FeatureScope
import com.github.sidky.comical.data.ComicsRepository
import com.github.sidky.comical.data.model.Publisher
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

sealed class PublishersInteraction: BaseInteraction {
    class PublisherList(val publishers: List<Publisher>): PublishersInteraction()
    class PublisherSelected(val id: String): PublishersInteraction()
}

@ExperimentalCoroutinesApi
@FeatureScope
class PublishersPresenter @Inject constructor(var repository: ComicsRepository): ViewModel(), ArchPresenter<PublishersInteraction> {

    private val interactionEvents = Channel<PublishersInteraction>()

    override fun interactions(): Channel<PublishersInteraction> = interactionEvents

    init {
        register()

//        testFeed()
    }

    @ExperimentalCoroutinesApi
    fun register() {
        viewModelScope.launch {
            repository.availables(viewModelScope).collect {
                interactionEvents.send(PublishersInteraction.PublisherList(it))
            }
        }
    }

    fun publisherSelected(id: String) {
        viewModelScope.launch {
            interactionEvents.send(PublishersInteraction.PublisherSelected(id))
        }
    }

    fun testFeed() {
        viewModelScope.launch {
            var pageNo = 1
            for (page in repository.pagedComicsFeed("xkcd", 2, 1, viewModelScope)) {
                Timber.i("PAGE: $pageNo")
                pageNo++

                for (entry in page) {
                    Timber.i("ENTRY: $entry")
                }
            }
        }
    }
}

