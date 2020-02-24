package com.github.sidky.comical.data.model

import java.time.LocalDateTime

data class Publisher(
    val id: String,
    val name: String,
    val description: String?,
    val author: String?,
    val lastUpdated: LocalDateTime,
    val origin: String,
    val icon: String?,
    val subscribed: Boolean
)