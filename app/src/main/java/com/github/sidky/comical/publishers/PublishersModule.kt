package com.github.sidky.comical.publishers

import androidx.lifecycle.ViewModel
import com.github.sidky.comical.common.FeatureScope
import com.github.sidky.comical.viewmodel.DaggerViewModelFactory
import com.github.sidky.comical.viewmodel.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Provider

@Module
class PublishersModule {

    @ExperimentalCoroutinesApi
    @Provides
    @IntoMap
    @ViewModelKey(PublishersPresenter::class)
    fun bindPublisherPresenter(presenter: PublishersPresenter): ViewModel = presenter

    @Provides
    @FeatureScope
    fun providesViewModelFactory(providerMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): DaggerViewModelFactory = DaggerViewModelFactory(providerMap)
}