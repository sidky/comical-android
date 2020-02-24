package com.github.sidky.comical.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.Coil
import coil.api.load
import com.github.sidky.comical.R
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@BindingAdapter("app:durationSince")
fun untilDate(view: TextView, dateTime: LocalDateTime) {
    val now = LocalDateTime.now()
    val duration = Duration.between(dateTime, now)
    val context = view.context
    val resources = context.resources

    if (duration.isNegative) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE

        if (duration.get(ChronoUnit.YEARS) > 0) {
            view.setText(resources.getQuantityText(R.plurals.duration_years_ago, duration.get(ChronoUnit.YEARS).toInt()))
        } else if (duration.get(ChronoUnit.MONTHS) > 0) {
            view.setText(resources.getQuantityText(R.plurals.duration_months_ago, duration.get(ChronoUnit.MONTHS).toInt()))
        } else if (duration.get(ChronoUnit.DAYS) > 0) {
            view.setText(resources.getQuantityText(R.plurals.duration_months_ago, duration.get(ChronoUnit.DAYS).toInt()))
        } else if (duration.get(ChronoUnit.HOURS) > 0) {
            view.setText(resources.getQuantityText(R.plurals.duration_hours_ago, duration.get(ChronoUnit.HOURS).toInt()))
        } else {
            view.setText(resources.getQuantityText(R.plurals.duration_minutes_ago, duration.get(ChronoUnit.DAYS).toInt()))
        }
    }
}

@BindingAdapter("app:remoteSrc")
fun remoteSrc(view: ImageView, url: String) {
    view.load(url) {

    }
}