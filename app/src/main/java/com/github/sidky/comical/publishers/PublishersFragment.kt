package com.github.sidky.comical.publishers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sidky.comical.R
import com.github.sidky.comical.arch.ArchView
import com.github.sidky.comical.common.UI
import com.github.sidky.comical.databinding.FragmentPublishersBinding
import com.github.sidky.comical.fragment.InjectLoggedIn
import com.github.sidky.comical.fragment.InjectableFragment
import com.github.sidky.comical.loggedin.LoggedInComponent
import com.github.sidky.comical.util.LifecycleJobDisposable
import com.github.sidky.comical.viewmodel.DaggerViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class PublishersFragment: InjectableFragment(), ArchView<PublishersInteraction, PublishersPresenter>, InjectLoggedIn {

    @Inject
    lateinit var viewModelFactory: DaggerViewModelFactory

    @Inject @UI
    lateinit var uiScope: CoroutineScope

    private lateinit var binding: FragmentPublishersBinding

    private val viewModelProvider by lazy {
        ViewModelProvider(this, viewModelFactory)
    }

    private val disposable by lazy {
        LifecycleJobDisposable(lifecycle)
    }

    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private val adapter: PublisherAdapter by lazy {
        PublisherAdapter(presenter())
    }

    override fun onResume() {
        super.onResume()

        disposable.addUntilPause(register())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_publishers, container, false)

        binding.comicsList.layoutManager = layoutManager
        binding.comicsList.adapter = adapter

        return binding.root
    }

    override fun presenter(): PublishersPresenter = viewModelProvider.get(PublishersPresenter::class.java)

    override fun event(event: PublishersInteraction) {
        when (event) {
            is PublishersInteraction.PublisherList -> {
                adapter.update(event.publishers)
            }
            is PublishersInteraction.PublisherSelected -> {
                val action = PublishersFragmentDirections.actionPublishersFragmentToComicsFeedFragment(event.id)
                findNavController().navigate(action)
            }
            else -> Timber.e("PublishersFragment doesn't know how to handle interactions of type ${event}")
        }
    }

    override fun completeInject(component: LoggedInComponent) {
        val localComponent = component
            .publishersComponent()
            .get()
            .withPublsihersModule(PublishersModule())
            .build()
        localComponent.inject(this)
    }
}