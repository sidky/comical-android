package com.github.sidky.comical.arch

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import timber.log.Timber

interface ArchView<E: BaseInteraction, T: ArchPresenter<E>> : LifecycleOwner {
    fun presenter(): T

    fun event(event: E)

    fun attachPresenter() {
        lifecycle.addObserver(presenter())
    }

    fun register(): Job {
        return lifecycleScope.launch {
            while (isActive) {
                try {
                    val e = presenter().interactions().receive()
                    dispatchEvent(e)
                } catch(ex: CancellationException) {
                    Timber.e("Scope for listening to events has been cancelled.")
                }
            }
        }
    }

    private fun dispatchEvent(event: E) {
        CoroutineScope(Dispatchers.Main).launch {
            event(event)
        }
    }
}