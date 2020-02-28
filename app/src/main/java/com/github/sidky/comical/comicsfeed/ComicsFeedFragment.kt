package com.github.sidky.comical.comicsfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sidky.comical.R
import com.github.sidky.comical.arch.ArchView
import com.github.sidky.comical.databinding.FragmentComicsFeedBinding
import com.github.sidky.comical.databinding.ItemComicsFeedBinding
import com.github.sidky.comical.fragment.InjectLoggedIn
import com.github.sidky.comical.fragment.InjectableFragment
import com.github.sidky.comical.loggedin.LoggedInComponent
import com.github.sidky.comical.util.LifecycleJobDisposable
import com.github.sidky.comical.viewmodel.DaggerViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalArgumentException
import javax.inject.Inject

class ComicsFeedFragment: InjectableFragment(), InjectLoggedIn, ArchView<ComicsFeedInteractions, ComicsFeedPresenter> {

    @Inject
    lateinit var daggerViewModelFactory: DaggerViewModelFactory

    lateinit var binding: FragmentComicsFeedBinding

    private val adapter: ComicsFeedAdapter by lazy {
        ComicsFeedAdapter()
    }

    private val layoutManager by lazy {
        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private val viewModelProvider by lazy {
        ViewModelProvider(this, daggerViewModelFactory)
    }

    private val disposable by lazy {
        LifecycleJobDisposable(lifecycle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comics_feed, container, false)

        binding.feed.adapter = adapter
        binding.feed.layoutManager = layoutManager

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        disposable.addUntilPause(register())
    }

    override fun completeInject(component: LoggedInComponent) {
        val arg = arguments
        val parameter = if (arg != null) {
            ComicsFeedFragmentArgs.fromBundle(arg)
        } else {
            throw IllegalArgumentException("No argument given to ComicsFeedFragment")
        }
        component.comicsFeedComponent().get()
            .withParameter(parameter)
            .withComicsFeedModule(ComicsFeedModule())
            .build()
            .inject(this)
    }

    @ExperimentalCoroutinesApi
    override fun presenter(): ComicsFeedPresenter =
        viewModelProvider.get(ComicsFeedPresenter::class.java)

    override fun event(event: ComicsFeedInteractions) {
        when (event) {
            is ComicsFeedInteractions.Feed -> {
                event.feed.forEach { Timber.i(it.toString()) }
                Timber.i("${event.feed.size} elements")
                loadMore()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun loadMore() {
        lifecycleScope.launch {
            delay(10000)
            presenter().loadMore()
        }
    }
}