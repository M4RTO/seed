package com.fif.payments.seed.config.rest

import com.fif.payments.seed.adapter.rest.exception.RestClientGenericException
import com.fif.payments.seed.adapter.rest.exception.TimeoutRestClientException
import com.fif.payments.seed.config.SPError
import com.fif.payments.seed.config.exception.ResourceNotFoundException
import com.fif.payments.seed.shared.CompanionLogger
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler
import java.util.stream.Collectors

class RestTemplateErrorHandler : ResponseErrorHandler {


    override fun hasError(response: ClientHttpResponse): Boolean {
        return response.statusCode.isError
    }

    override fun handleError(response: ClientHttpResponse) {
        response.body.bufferedReader().lines()
            .collect(Collectors.joining(NEW_LINE_DELIMITER))
            .replace(NEW_LINE_DELIMITER, SPACE_DELIMITER)
            .log { warn("Error HTTP {} {}", response.statusCode, it) }


        throw when (response.statusCode) {
            HttpStatus.NOT_FOUND -> ResourceNotFoundException(SPError.RESOURCE_NOT_FOUND.errorCode, SPError.RESOURCE_NOT_FOUND.defaultMessage)
            HttpStatus.REQUEST_TIMEOUT -> TimeoutRestClientException(SPError.TIMEOUT_FOUND.errorCode, SPError.TIMEOUT_FOUND.defaultMessage)
            HttpStatus.GATEWAY_TIMEOUT -> TimeoutRestClientException(SPError.TIMEOUT_FOUND.errorCode, SPError.TIMEOUT_FOUND.defaultMessage)
            else -> RestClientGenericException(SPError.INTERNAL_ERROR.errorCode, SPError.INTERNAL_ERROR.defaultMessage)
        }
    }

    companion object: CompanionLogger() {
        private const val NEW_LINE_DELIMITER = "\n"
        private const val SPACE_DELIMITER = " "
    }

}
