package com.github.sidky.comical.login

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.github.sidky.comical.arch.ArchPresenter
import com.github.sidky.comical.arch.BaseInteraction
import com.github.sidky.comical.auth.AuthRepository
import com.github.sidky.comical.auth.SignInResult
import com.github.sidky.comical.auth.UserStatus
import com.github.sidky.comical.common.IO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginInteraction: BaseInteraction {
    object SuccessfulLogin : LoginInteraction()
    data class Error(val message: String): LoginInteraction()
}

class LoginPresenter @Inject constructor(
    private val authRepository: AuthRepository,
    @IO private val scope: CoroutineScope
): ArchPresenter<LoginInteraction> {
    val event = Channel<LoginInteraction>()

    override fun interactions() = event

    var job: Job? = null

    suspend fun handleLogin(result: SignInResult) {
        if (result is SignInResult.Failure) {
                event.send(LoginInteraction.Error("Unable to log in"))
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun register() {
        val channel = authRepository.authenticationState.openSubscription()
        job = scope.launch {
            try {
                while (isActive) {
                    val state = channel.receive()

                    if (state is UserStatus.AuthModel) {
                        event.send(LoginInteraction.SuccessfulLogin)
                    }
                }
            } finally {
                channel.cancel()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unregister() {
        job?.cancel()
        job = null
    }
}