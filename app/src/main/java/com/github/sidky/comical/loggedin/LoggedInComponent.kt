package com.github.sidky.comical.loggedin

import com.github.sidky.comical.comicsfeed.ComicsFeedComponent
import com.github.sidky.comical.common.LoggedInScope
import com.github.sidky.comical.data.StorageModule
import com.github.sidky.comical.login.LoginFragment
import com.github.sidky.comical.publishers.PublishersComponent
import com.github.sidky.comical.publishers.PublishersFragment
import dagger.Subcomponent
import javax.inject.Provider

@Subcomponent(modules = [LoggedInModule::class, StorageModule::class])
@LoggedInScope
interface LoggedInComponent {

    fun publishersComponent(): Provider<PublishersComponent.Builder>
    fun comicsFeedComponent(): Provider<ComicsFeedComponent.Builder>

    @Subcomponent.Builder
    interface Builder {
        fun withLoggedInModule(module: LoggedInModule): Builder
        fun withStorageModule(module: StorageModule): Builder
        fun build(): LoggedInComponent
    }
}