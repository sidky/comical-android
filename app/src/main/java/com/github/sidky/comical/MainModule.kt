package com.github.sidky.comical

import com.github.sidky.comical.common.ApplicationScope
import com.github.sidky.comical.loggedin.LoggedInComponent
import com.github.sidky.comical.login.LoginComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LoggedInReady

@Module(subcomponents = [LoginComponent::class, LoggedInComponent::class])
class MainModule(private val activity: MainActivity) {

    private val loggedInReadyChannel = BroadcastChannel<Boolean>(Channel.CONFLATED)

    @Provides
    @ApplicationScope
    fun providesActivity() = activity

    @Provides
    @ApplicationScope
    @LoggedInReady
    fun providesLoggedInReadyChannel(): BroadcastChannel<Boolean> = loggedInReadyChannel
}