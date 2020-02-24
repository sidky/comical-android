package com.github.sidky.comical.data.model

import java.time.LocalDateTime

data class FeedItem(val id: String,
                    val permalink: String,
                    val title: String,
                    val description: String,
                    val published: LocalDateTime?,
                    val images: List<String>)