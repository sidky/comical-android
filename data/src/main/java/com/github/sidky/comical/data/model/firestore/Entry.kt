package com.github.sidky.comical.data.model.firestore

import com.github.sidky.comical.data.model.FeedItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class Entry(
    val id: String = "",
    val permalink: String? = null,
    val title: String? = null,
    val published: Long? = null,
    val description: String? = null,
    val images: List<String?> = emptyList()
) {
    fun toFeedItem(): FeedItem =
        FeedItem(
            id = this.id,
            permalink = this.permalink ?: "",
            title = this.title ?: "",
            description = this.description ?: "",
            published = this.published?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.of("UTC")) },
            images = this.images.filterNotNull())
}