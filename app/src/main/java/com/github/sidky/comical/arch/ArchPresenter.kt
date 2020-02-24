package com.github.sidky.comical.arch

import androidx.lifecycle.LifecycleObserver
import kotlinx.coroutines.channels.Channel

interface ArchPresenter<T : BaseInteraction>: LifecycleObserver{
    fun interactions(): Channel<T>
}