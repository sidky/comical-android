package com.github.sidky.comical.login

import com.github.sidky.comical.common.FeatureScope
import dagger.Subcomponent

@Subcomponent
@FeatureScope
interface LoginComponent {

    fun inject(fragment: LoginFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): LoginComponent
    }
}