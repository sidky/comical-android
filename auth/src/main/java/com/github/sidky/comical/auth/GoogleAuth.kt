package com.github.sidky.comical.auth

import android.content.Intent
import com.github.sidky.comical.common.FeatureScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.lang.Exception
import java.lang.IllegalStateException
import javax.inject.Inject
import kotlin.coroutines.resume

sealed class SignInResult {
    data class Success(val user: FirebaseUser): SignInResult()
    data class Failure(val ex: Exception): SignInResult()
}

@FeatureScope
class GoogleAuth @Inject constructor(private val client: GoogleSignInClient,
                                     private val auth: FirebaseAuth) {

    fun signInIntent() = client.signInIntent

    @Throws(ApiException::class)
    suspend fun completeAuthentication(data: Intent?): SignInResult =
        suspendCancellableCoroutine<SignInResult> { coroutine ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)


            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (task.isSuccessful) {
                        Timber.d("signInWithCredential:success")
                        val user = auth.currentUser
                        if (user != null) {
                            coroutine.resume(SignInResult.Success(user))
                        } else {
                            coroutine.resume(SignInResult.Failure(IllegalStateException("user is null")))
                        }
                    } else {
                        Timber.w(task.exception, "signInWithCredential:failure")
                        coroutine.resume(
                            SignInResult.Failure(
                                task.exception
                                    ?: IllegalStateException("null exception")
                            )
                        )
                    }
                }
        }

    fun logout() {
        auth.signOut()
    }
}