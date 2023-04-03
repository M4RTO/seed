package com.fif.payments.seed.extensions

fun String.trimSQL() = lines()
        .filter(String::isNotBlank)
        .joinToString(separator = " ") { it.trim() }
