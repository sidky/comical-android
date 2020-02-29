package com.github.sidky.comical.comicsitem

import com.github.sidky.comical.common.FeatureScope
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [ComicsItemModule::class])
@FeatureScope
interface ComicsItemComponent {
    fun inject(fragment: ComicsItemFragment)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun withArguments(args: ComicsItemFragmentArgs): Builder
        fun withComicsItemModule(module: ComicsItemModule): Builder
        fun build(): ComicsItemComponent
    }
}