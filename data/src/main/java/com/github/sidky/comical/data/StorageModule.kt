package com.github.sidky.comical.data

import com.github.sidky.comical.common.ApplicationScope
import com.github.sidky.comical.common.LoggedInScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides

@Module
class StorageModule {

    @Provides @LoggedInScope
    fun provideFirestore() = FirebaseFirestore.getInstance()
}