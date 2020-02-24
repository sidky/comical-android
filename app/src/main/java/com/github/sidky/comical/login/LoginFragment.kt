package com.github.sidky.comical.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.sidky.comical.LoggedInReady
import com.github.sidky.comical.MainComponent
import com.github.sidky.comical.R
import com.github.sidky.comical.arch.ArchView
import com.github.sidky.comical.arch.CanProvideMainComponent
import com.github.sidky.comical.auth.AuthRepository
import com.github.sidky.comical.auth.GoogleAuth
import com.github.sidky.comical.auth.UserStatus
import com.github.sidky.comical.common.FeatureScope
import com.github.sidky.comical.common.UI
import com.github.sidky.comical.databinding.FragmentLoginBinding
import com.github.sidky.comical.fragment.InjectLoggedOut
import com.github.sidky.comical.fragment.InjectableFragment
import com.github.sidky.comical.publishers.PublishersFragment
import com.github.sidky.comical.util.LifecycleJobDisposable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber
import javax.inject.Inject

interface LoginActions {
    fun login()
}
@FeatureScope
class LoginFragment() : InjectableFragment(), ArchView<LoginInteraction, LoginPresenter>, LoginActions, InjectLoggedOut {

    @Inject lateinit var presenter: LoginPresenter
    @Inject lateinit var googleAuth: GoogleAuth

    @ExperimentalCoroutinesApi
    @Inject @LoggedInReady lateinit var loggedInReadyChannel: BroadcastChannel<Boolean>

    @Inject @UI
    lateinit var uiScope: CoroutineScope

    lateinit var component: LoginComponent

    lateinit var binding: FragmentLoginBinding

    private val disposable: LifecycleJobDisposable by lazy {
        LifecycleJobDisposable(lifecycle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login, container, false)
        binding.actions = this
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onResume() {
        super.onResume()
        disposable.addUntilPause(register())

        val job = lifecycleScope.launch {
            val channel = loggedInReadyChannel.openSubscription()
            try {
                while (isActive) {
                    val state = channel.receive()
                    if (state) {
                        postLoggedIn()
                    }
                }
            } catch (ex: CancellationException) {
                Timber.e("Scope cancelled")
            } finally {
                channel.cancel()
            }
        }
        disposable.addUntilPause(job)
    }
    override fun login() {
        startActivityForResult(googleAuth.signInIntent(), RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            lifecycleScope.launch {
                val result = googleAuth.completeAuthentication(data)
                presenter.handleLogin(result)
            }
        }
    }

    override fun presenter(): LoginPresenter = presenter

    override fun event(event: LoginInteraction) {
        when (event) {
            is LoginInteraction.Error -> Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
            is LoginInteraction.SuccessfulLogin -> {
                Timber.i("Logged in")
            }
        }
    }

    override fun completeInjection(component: MainComponent) {
        val fragmentComponent = component.loginFragmentComponent().get().build()
        fragmentComponent.inject(this)
        attachPresenter()
    }

    private fun postLoggedIn() {
        uiScope.launch {
            val action = LoginFragmentDirections.actionLoginFragmentToPublishersFragment()
            findNavController().navigate(action)
        }

    }

    companion object {
        private val RC_SIGN_IN = 5001
    }
}