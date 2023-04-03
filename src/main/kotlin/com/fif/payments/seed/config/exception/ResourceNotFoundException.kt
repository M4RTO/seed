package com.fif.payments.seed.config.exception

open class ResourceNotFoundException(
        errorCode: Int,
        message: String,
        cause: Throwable? = null
) : GenericException(
        errorCode,
        message,
        cause
)
