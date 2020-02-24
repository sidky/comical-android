package com.github.sidky.comical

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.github.sidky.comical.arch.ArchView
import com.github.sidky.comical.arch.CanProvideLoggedInComponent
import com.github.sidky.comical.arch.CanProvideMainComponent
import com.github.sidky.comical.auth.AuthModule
import com.github.sidky.comical.auth.AuthRepository
import com.github.sidky.comical.auth.UserStatus
import com.github.sidky.comical.common.IO
import com.github.sidky.comical.common.UI
import com.github.sidky.comical.data.StorageModule
import com.github.sidky.comical.loggedin.LoggedInComponent
import com.github.sidky.comical.loggedin.LoggedInModule
import com.github.sidky.comical.login.LoginFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class MainActivity() : AppCompatActivity(), CanProvideMainComponent, CanProvideLoggedInComponent {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject @IO
    lateinit var ioScope: CoroutineScope

    @Inject @UI
    lateinit var uiScope: CoroutineScope

    @Inject @LoggedInReady
    lateinit var loggedInReadyChannel: BroadcastChannel<Boolean>

    lateinit var component: MainComponent

    var loggedInComponent: LoggedInComponent? = null

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeDagger()

        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            val channel = authRepository.authenticationState.openSubscription()
            try {
                while (isActive) {
                    val status = channel.receive()
                    if (status == UserStatus.Unauthenticated) {
                        loggedInComponent = null
                        loggedInReadyChannel.send(false)
                    } else {
                        if (loggedInComponent == null) {
                            loggedInComponent = component.loggedInComponentProvider()
                                .get()
                                .withLoggedInModule(LoggedInModule(status))
                                .withStorageModule(StorageModule())
                                .build()
                            loggedInReadyChannel.send(true)
                        }
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex)
            } finally {
                channel.cancel()
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        when (fragment) {
            is LoginFragment -> {
                val subComponent = component.loginFragmentComponent().get().build()
                subComponent.inject((fragment))
            }
        }

        if (fragment is ArchView<*, *>) {
            fragment.lifecycle.addObserver(fragment.presenter())
        }
    }

    private fun initializeDagger() {
        component = DaggerMainComponent.builder()
            .withMainModule(MainModule(this))
            .withAuthModule(
                AuthModule(this, getString(R.string.default_web_client_id))
            )
            .build()
        component.inject(this)
    }

    private fun logout() {
        uiScope.launch {
            findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
        }
    }

    override fun mainComponent(): MainComponent = component

    override fun loggedInComponent(): LoggedInComponent = loggedInComponent!!
}