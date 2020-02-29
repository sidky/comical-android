package com.github.sidky.comical.comicsitem

import androidx.lifecycle.ViewModel
import com.github.sidky.comical.common.FeatureScope
import com.github.sidky.comical.viewmodel.DaggerViewModelFactory
import com.github.sidky.comical.viewmodel.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider

@Module
class ComicsItemModule {

    @Provides
    @IntoMap
    @ViewModelKey(ComicsItemPresenter::class)
    fun bindComicsItemPresenter(presenter: ComicsItemPresenter): ViewModel = presenter

    @Provides
    @FeatureScope
    fun providesViewModelFactory(providerMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): DaggerViewModelFactory =
        DaggerViewModelFactory(providerMap)
}