package com.github.sidky.comical.common

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IO

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UI

@Module
class ScopeModule {

    @IO @Provides
    fun providesIOScope() = CoroutineScope(Dispatchers.IO)

    @UI @Provides
    fun providesUIScope() = CoroutineScope(Dispatchers.Main)
}