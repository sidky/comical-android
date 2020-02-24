package com.github.sidky.comical.comicsfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sidky.comical.R
import com.github.sidky.comical.arch.ArchView
import com.github.sidky.comical.databinding.FragmentComicsFeedBinding
import com.github.sidky.comical.databinding.ItemComicsFeedBinding
import com.github.sidky.comical.fragment.InjectLoggedIn
import com.github.sidky.comical.fragment.InjectableFragment
import com.github.sidky.comical.loggedin.LoggedInComponent
import com.github.sidky.comical.viewmodel.DaggerViewModelFactory
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

    override fun completeInject(component: LoggedInComponent) {
        component.comicsFeedComponent().get().withComicsFeedModule(ComicsFeedModule()).build().inject(this)
    }

    override fun presenter(): ComicsFeedPresenter =
        viewModelProvider.get(ComicsFeedPresenter::class.java)

    override fun event(event: ComicsFeedInteractions) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}