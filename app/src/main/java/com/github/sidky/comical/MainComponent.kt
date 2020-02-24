package com.github.sidky.comical

import com.github.sidky.comical.auth.AuthModule
import com.github.sidky.comical.common.ApplicationScope
import com.github.sidky.comical.common.ScopeModule
import com.github.sidky.comical.loggedin.LoggedInComponent
import com.github.sidky.comical.login.LoginComponent
import dagger.Component
import javax.inject.Provider

@Component(modules = [AuthModule::class, MainModule::class, ScopeModule::class])
@ApplicationScope
interface MainComponent {

    fun inject(activity: MainActivity)

    fun loginFragmentComponent(): Provider<LoginComponent.Builder>

    fun loggedInComponentProvider(): Provider<LoggedInComponent.Builder>

    @Component.Builder
    interface Builder {
        fun withMainModule(module: MainModule): Builder

        fun withAuthModule(module: AuthModule): Builder

        fun build(): MainComponent
    }
}