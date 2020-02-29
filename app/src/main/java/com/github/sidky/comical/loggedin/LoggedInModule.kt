package com.github.sidky.comical.loggedin

import com.github.sidky.comical.auth.UserStatus
import com.github.sidky.comical.comicsfeed.ComicsFeedComponent
import com.github.sidky.comical.comicsitem.ComicsItemComponent
import com.github.sidky.comical.publishers.PublishersComponent
import dagger.Module
import dagger.Provides

@Module(subcomponents = [PublishersComponent::class, ComicsFeedComponent::class, ComicsItemComponent::class])
class LoggedInModule(private val userStatus: UserStatus) {

    @Provides
    fun providesUser() = userStatus
}