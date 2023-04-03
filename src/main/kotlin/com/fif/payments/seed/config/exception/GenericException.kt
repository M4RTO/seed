package com.fif.payments.seed.config.exception

abstract class GenericException(
        val errorCode: Int,
        message: String,
        cause: Throwable? = null
) : RuntimeException(
        message,
        cause
)
