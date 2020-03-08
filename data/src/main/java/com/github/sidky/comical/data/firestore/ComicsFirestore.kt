package com.github.sidky.comical.data.firestore

import com.github.sidky.comical.common.LoggedInScope
import com.github.sidky.comical.data.model.FeedItem
import com.github.sidky.comical.data.model.firestore.ComicsInfo
import com.github.sidky.comical.data.model.firestore.Entry
import com.github.sidky.comical.data.model.Publisher
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

@LoggedInScope
class ComicsFirestore @Inject constructor(private val db: FirebaseFirestore) {

    @ExperimentalCoroutinesApi
    suspend fun availables(scope: CoroutineScope) = channelFlow<List<Publisher>> {
        val registration = db.collection("comics-info")
            .whereEqualTo("enabled", true)
            .addSnapshotListener { snapshot, exception ->
                val comics = mutableListOf<Publisher>()
                if (snapshot != null) {
                    for (document in snapshot.documents) {
                        val info = document.toObject(ComicsInfo::class.java)
                        if (info != null) {
                            comics.add(info.toPublisher())
                        }
                    }
                }
                scope.launch {
                    send(comics.toList())
                }
            }

        awaitClose {
            registration.remove()
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun listenOnFeedUpdates(id: String, scope: CoroutineScope) = channelFlow<Int> {
        val registration = referenceToComics(id).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (querySnapshot != null) {
                scope.launch {
                    send(querySnapshot.size())
                }
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun pagedFeed(scope: CoroutineScope, id: String, pageSize: Int, buffer: Int = 0) = scope.produce<List<FeedItem>>(capacity = buffer) {

        var lastSnapshot: QuerySnapshot? = null
        var lastDocument: DocumentSnapshot? = null
        var isComplete = false

        while (isActive && !isComplete) {
            Timber.e("SNAPSHOT: $lastSnapshot")
            val query = referenceToComics(id)
                .orderBy("published", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .let {
                    val document = lastDocument
                    if (document != null) {
                        //it.startAfter(lastSnapshot)
                        it.startAfter(document)
                    } else {
                        it
                    }
                }

            val channel = Channel<QueryResult>()

            query.get().addOnCompleteListener {
                val snapshot = it.result

                val result = if (it.isSuccessful && snapshot != null) {
                    val entries = mutableListOf<FeedItem>()
                    val documents = it.result?.documents

                    for (document in documents ?: emptyList()) {
                        val entry = document.toObject(Entry::class.java)
                        if (entry != null) {
                            entries.add(entry.toFeedItem())
                        } else {
                            Timber.e("Unable to parse entry: $document")
                        }
                    }
                    if (entries.isEmpty() || documents == null) {
                        QueryResult.Complete
                    } else {
                        val lastItem = documents.last()
                        QueryResult.Success(entries, lastItem, snapshot)
                    }
                } else {
                    QueryResult.Failure(it.exception)
                }

                try {
                    scope.launch {
                        channel.send(result)
                    }
                } finally {
                    channel.close()
                }
            }

            val queryResult = channel.receive()

            if (queryResult is QueryResult.Success) {
                lastSnapshot = queryResult.snapshot
                lastDocument = queryResult.lastDocument
                send(queryResult.entries)

                if (queryResult.entries.isEmpty()) {
                    isComplete = true
                }
            } else if (queryResult is QueryResult.Failure){
                Timber.e(queryResult.ex, "Unable to load")
            } else {
                isComplete = true
            }
        }
    }

    suspend fun availableSubscriptions(): List<ComicsInfo> = suspendCancellableCoroutine { t ->
        db.collection("comics-info").whereEqualTo("enabled", true).get()
            .addOnSuccessListener { result ->
                val comics = mutableListOf<ComicsInfo>()
                for (document in result) {
                    val info = document.toObject(ComicsInfo::class.java)
                    comics.add(info)
                }
                t.resumeWith(Result.success(comics))
            }
            .addOnFailureListener {
                t.resumeWithException(it)
            }
    }

    suspend fun comicsItem(feedId: String, comicsId: String): FeedItem = suspendCancellableCoroutine { t ->
        referenceToComics(feedId).whereEqualTo("id", comicsId).get()
            .addOnSuccessListener {
                val info = it.toObjects(Entry::class.java)[0]
                t.resumeWith(Result.success(info.toFeedItem()))
            }
            .addOnFailureListener {
                t.resumeWithException(it)
            }
//        referenceToComics(comicsId).whereEqualTo("")
    }

    private sealed class QueryResult {
        class Success(val entries: List<FeedItem>, val lastDocument: DocumentSnapshot, val snapshot: QuerySnapshot): QueryResult()
        object Complete: QueryResult()
        class Failure(val ex: Exception?): QueryResult()
    }

    private fun referenceToComics(id: String) =
        db.collection("comics").document(id).collection("entries")
}