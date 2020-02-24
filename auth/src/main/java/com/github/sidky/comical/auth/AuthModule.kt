package com.github.sidky.comical.auth

import android.app.Activity
import com.github.sidky.comical.common.ApplicationScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class AuthModule(private val activity: Activity, private val webClientId: String) {

    @Provides
    @ApplicationScope
    fun providesGoogleSignInOptions() =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

    @Provides
    @ApplicationScope
    fun providesGoogleSignInClient(gso: GoogleSignInOptions) =
        GoogleSignIn.getClient(activity, gso)

    @Provides
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()
}