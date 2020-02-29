package com.github.sidky.comical.comicsitem

import androidx.lifecycle.ViewModel
import com.github.sidky.comical.arch.ArchPresenter
import com.github.sidky.comical.arch.BaseInteraction
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

sealed class ComicsItemInteractions: BaseInteraction {

}

class ComicsItemPresenter @Inject constructor(): ArchPresenter<ComicsItemInteractions>, ViewModel() {

    private val interactionsChannel = Channel<ComicsItemInteractions>()

    override fun interactions(): Channel<ComicsItemInteractions> = interactionsChannel
}