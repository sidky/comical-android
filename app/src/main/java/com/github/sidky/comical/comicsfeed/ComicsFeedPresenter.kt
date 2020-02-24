package com.github.sidky.comical.comicsfeed

import androidx.lifecycle.ViewModel
import com.github.sidky.comical.arch.ArchPresenter
import com.github.sidky.comical.arch.BaseInteraction
import com.github.sidky.comical.common.FeatureScope
import com.github.sidky.comical.data.model.firestore.Entry
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

sealed class ComicsFeedInteractions: BaseInteraction {
    data class Feed(val feed: List<Entry>, val isLoading: Boolean, val canRefresh: Boolean): ComicsFeedInteractions()
    class Error(val message: String): ComicsFeedInteractions()
}

@FeatureScope
class ComicsFeedPresenter @Inject constructor(): ViewModel(), ArchPresenter<ComicsFeedInteractions> {

    private val comicsFeed = mutableListOf<Entry>()

    private val interactionChannel = Channel<ComicsFeedInteractions>()

    override fun interactions(): Channel<ComicsFeedInteractions> = interactionChannel

}