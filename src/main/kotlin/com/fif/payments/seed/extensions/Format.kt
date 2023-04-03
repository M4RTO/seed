package com.fif.payments.seed.extensions

fun String.toSnakeCase(): String =
        this.map {
            if (it.isUpperCase()) "_" + it.lowercaseChar()
            else it.toString()
        }.joinToString(separator = "")
