package com.github.sidky.comical.auth

import com.github.sidky.comical.common.ApplicationScope
import com.github.sidky.comical.common.IO
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

enum class AuthProvider {
    GOOGLE
}

sealed class UserStatus {
    data class AuthModel(val provider: AuthProvider, val userId: String, val displayName: String, val email: String) : UserStatus()
    object Unauthenticated : UserStatus()
}

@ApplicationScope
class AuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth,
                                         @IO private val ioScope: CoroutineScope) {
    val authenticationState = BroadcastChannel<UserStatus>(Channel.CONFLATED)


    init {
        firebaseAuth.addAuthStateListener {
            ioScope.launch {
                val user = it.currentUser
                if (user == null || user.isAnonymous == true) {
                    authenticationState.send(UserStatus.Unauthenticated)
                } else {
                    val status = UserStatus.AuthModel(
                        AuthProvider.GOOGLE,
                        user.uid,
                        user.displayName ?: "Unknown",
                        user.email ?: "")
                    authenticationState.send(status)
                }
            }
        }
    }

    fun logout() = firebaseAuth.signOut()
}