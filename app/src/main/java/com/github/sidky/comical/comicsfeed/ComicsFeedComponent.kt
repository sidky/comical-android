package com.github.sidky.comical.comicsfeed

import com.github.sidky.comical.common.FeatureScope
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [ComicsFeedModule::class])
@FeatureScope
interface ComicsFeedComponent {

    fun inject(fragment: ComicsFeedFragment)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun withParameter(parameter: ComicsFeedFragmentArgs): Builder
        fun withComicsFeedModule(module: ComicsFeedModule): Builder
        fun build(): ComicsFeedComponent
    }
}