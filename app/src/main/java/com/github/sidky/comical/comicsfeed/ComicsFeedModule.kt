package com.github.sidky.comical.comicsfeed

import androidx.lifecycle.ViewModel
import com.github.sidky.comical.common.FeatureScope
import com.github.sidky.comical.publishers.PublishersPresenter
import com.github.sidky.comical.viewmodel.DaggerViewModelFactory
import com.github.sidky.comical.viewmodel.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Provider

@Module
class ComicsFeedModule {
    @Provides
    @IntoMap
    @ViewModelKey(ComicsFeedPresenter::class)
    fun bindComicsFeedPresenter(presenter: ComicsFeedPresenter): ViewModel = presenter

    @Provides
    @FeatureScope
    fun providesViewModelFactory(providerMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): DaggerViewModelFactory = DaggerViewModelFactory(providerMap)
}