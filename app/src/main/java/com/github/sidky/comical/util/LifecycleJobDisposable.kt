package com.github.sidky.comical.util

import androidx.annotation.GuardedBy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.Job

class LifecycleJobDisposable(val lifecycle: Lifecycle): LifecycleObserver {

    @GuardedBy("untilPause")
    private val untilPause = mutableListOf<Job>()

    @GuardedBy("untilStop")
    private val untilStop = mutableListOf<Job>()

    @GuardedBy("untilDestroy")
    private val untilDestroy = mutableListOf<Job>()

    init {
        lifecycle.addObserver(this)
    }

    fun addUntilPause(job: Job) = addUntil(job, TerminatingEvent.PAUSE)
    fun addUntilStop(job: Job) = addUntil(job, TerminatingEvent.STOP)
    fun addUntilDestroy(job: Job) = addUntil(job, TerminatingEvent.DESTROY)

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun paused() {
        synchronized(this) {
            disposeAll(untilPause)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopped() {
        synchronized(this) {
            disposeAll(untilPause)
            disposeAll(untilStop)
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyed() {
        synchronized(this) {
            disposeAll(untilPause)
            disposeAll(untilStop)
            disposeAll(untilDestroy)
        }
    }

    private fun addUntil(job: Job, terminatingEvent: TerminatingEvent) {
        when (terminatingEvent) {
            TerminatingEvent.PAUSE -> synchronized(untilPause) {
                untilPause.add(job)
            }
            TerminatingEvent.STOP -> synchronized(untilStop) {
                untilStop.add(job)
            }
            TerminatingEvent.DESTROY -> synchronized(untilDestroy) {
                untilDestroy.add(job)
            }
        }
    }

    private fun disposeAll(list: MutableList<Job>) {
        synchronized(list) {
            for (job in list) {
                job.cancel()
            }
            list.clear()
        }
    }

    enum class TerminatingEvent {
        PAUSE, STOP, DESTROY
    }
}