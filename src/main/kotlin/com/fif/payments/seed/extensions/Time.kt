package com.fif.payments.seed.extensions

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.toIsoString(): String = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(this)

fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this)
