package com.github.sidky.comical.arch

import com.github.sidky.comical.MainComponent
import com.github.sidky.comical.loggedin.LoggedInComponent

interface CanProvideMainComponent {
    fun mainComponent(): MainComponent
}

interface CanProvideLoggedInComponent {
    fun loggedInComponent(): LoggedInComponent
}