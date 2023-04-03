package com.fif.payments.seed.adapter.rest.exception

class TimeoutRestClientException(
        errorCode: Int,
        message: String,
        cause: Throwable? = null
) : RestClientGenericException(
        errorCode,
        message,
        cause
)


