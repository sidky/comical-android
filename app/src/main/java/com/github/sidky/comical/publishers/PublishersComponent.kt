package com.github.sidky.comical.publishers

import androidx.lifecycle.LifecycleCoroutineScope
import com.github.sidky.comical.common.FeatureScope
import com.github.sidky.comical.common.LoggedInScope
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [PublishersModule::class])
@FeatureScope
interface PublishersComponent {

    fun inject(fragment: PublishersFragment)

    @Subcomponent.Builder
    interface Builder {
        fun withPublsihersModule(module: PublishersModule): Builder
        fun build(): PublishersComponent
    }
}