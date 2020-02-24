package com.github.sidky.comical.fragment

import com.github.sidky.comical.loggedin.LoggedInComponent

interface InjectLoggedIn {
    fun completeInject(component: LoggedInComponent)
}