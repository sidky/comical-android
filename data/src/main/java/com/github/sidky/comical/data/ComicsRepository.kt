package com.github.sidky.comical.data

import com.github.sidky.comical.data.firestore.ComicsFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class ComicsRepository @Inject constructor(private val store: ComicsFirestore) {

    @ExperimentalCoroutinesApi
    suspend fun availables(scope: CoroutineScope) = store.availables(scope)

    @ExperimentalCoroutinesApi
    suspend fun pagedComicsFeed(id: String, pageSize: Int, bufferSize: Int, scope: CoroutineScope) = store.pagedFeed(scope, id, pageSize, bufferSize)
}