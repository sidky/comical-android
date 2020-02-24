package com.github.sidky.comical.data.model.firestore

import com.github.sidky.comical.data.model.Publisher
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class ComicsInfo(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String? = null,
    val origin: String = "",
    val lastUpdated: Timestamp? = null,
    val enabled: Boolean = false,
    val author: String? = null
) {
    fun toPublisher(): Publisher =
        Publisher(
            id = id,
            name = name,
            description = description,
            icon = icon,
            origin = origin,
            lastUpdated = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(lastUpdated?.seconds ?: 0),
                ZoneId.of("UTC")
            ),
            author = author,
            subscribed = false
        )
}