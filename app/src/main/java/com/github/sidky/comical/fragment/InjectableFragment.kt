package com.github.sidky.comical.fragment

import android.content.Context
import androidx.fragment.app.Fragment
import com.github.sidky.comical.MainActivity
import timber.log.Timber

abstract class InjectableFragment: Fragment() {
    final override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity) {
            if (this is InjectLoggedIn) {
                val component = context.loggedInComponent
                if (component == null) {
                    Timber.e("Invalid state. Expected LoggedInComponent to be populated in MainActivity")
                } else {
                    this.completeInject(component)
                }
            } else if (this is InjectLoggedOut) {
                this.completeInjection(context.component)
            } else {
                Timber.e("Invalid state. Fragment should either implement InjectLoggedIn or InjectLoggedOut")
            }
        } else {
            Timber.e("Invalid injection flow. Activity attched is: ${context} expected to be MainActivity")
        }
    }
}