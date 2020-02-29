package com.github.sidky.comical.comicsitem

import androidx.lifecycle.ViewModelProvider
import com.github.sidky.comical.arch.ArchView
import com.github.sidky.comical.fragment.InjectLoggedIn
import com.github.sidky.comical.fragment.InjectableFragment
import com.github.sidky.comical.loggedin.LoggedInComponent
import com.github.sidky.comical.util.LifecycleJobDisposable
import com.github.sidky.comical.viewmodel.DaggerViewModelFactory
import javax.inject.Inject

class ComicsItemFragment: InjectableFragment(), InjectLoggedIn, ArchView<ComicsItemInteractions, ComicsItemPresenter> {

    @Inject
    lateinit var daggerViewModelFactory: DaggerViewModelFactory

    private val disposable by lazy {
        LifecycleJobDisposable(lifecycle)
    }

    private val viewModelProvider by lazy {
        ViewModelProvider(this, daggerViewModelFactory)
    }

    override fun onResume() {
        super.onResume()

        disposable.addUntilPause(register())
    }

    override fun completeInject(component: LoggedInComponent) {
        val c = component.comicsItemComponent()
            .get()
            .withArguments(ComicsItemFragmentArgs.fromBundle(arguments!!))
            .withComicsItemModule(ComicsItemModule())
            .build()
        c.inject(this)
    }

    override fun presenter(): ComicsItemPresenter =
        viewModelProvider.get(ComicsItemPresenter::class.java)

    override fun event(event: ComicsItemInteractions) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}