package com.fif.payments.seed.adapter.rest.exception

import com.fif.payments.seed.config.exception.GenericException

open class RestClientGenericException(
        errorCode: Int,
        message: String,
        cause: Throwable? = null
) : GenericException(
        errorCode,
        message,
        cause
)
