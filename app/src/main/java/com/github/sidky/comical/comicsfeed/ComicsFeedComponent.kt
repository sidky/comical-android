package com.github.sidky.comical.comicsfeed

import com.github.sidky.comical.common.FeatureScope
import dagger.Subcomponent

@Subcomponent(modules = [ComicsFeedModule::class])
@FeatureScope
interface ComicsFeedComponent {

    fun inject(fragment: ComicsFeedFragment)

    @Subcomponent.Builder
    interface Builder {
        fun withComicsFeedModule(module: ComicsFeedModule): Builder
        fun build(): ComicsFeedComponent
    }
}