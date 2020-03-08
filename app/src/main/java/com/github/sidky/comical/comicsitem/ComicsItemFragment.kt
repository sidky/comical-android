package com.github.sidky.comical.comicsitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.sidky.comical.R
import com.github.sidky.comical.arch.ArchView
import com.github.sidky.comical.databinding.FragmentComicsItemBinding
import com.github.sidky.comical.fragment.InjectLoggedIn
import com.github.sidky.comical.fragment.InjectableFragment
import com.github.sidky.comical.loggedin.LoggedInComponent
import com.github.sidky.comical.util.LifecycleJobDisposable
import com.github.sidky.comical.viewmodel.DaggerViewModelFactory
import javax.inject.Inject

class ComicsItemFragment: InjectableFragment(), InjectLoggedIn, ArchView<ComicsItemInteractions, ComicsItemPresenter> {

    @Inject
    lateinit var daggerViewModelFactory: DaggerViewModelFactory

    private lateinit var binding: FragmentComicsItemBinding

    private val disposable by lazy {
        LifecycleJobDisposable(lifecycle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comics_item, container, false)
        return binding.root
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
        return when(event) {
            is ComicsItemInteractions.Item -> {
                binding.src = event.image
                binding.executePendingBindings()
            }
        }
    }
}