package com.fif.payments.seed.config

import brave.Tracer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fif.payments.seed.adapter.rest.exception.RestClientGenericException
import com.fif.payments.seed.adapter.rest.exception.TimeoutRestClientException
import com.fif.payments.seed.config.exception.GenericException
import com.fif.payments.seed.config.exception.ResourceNotFoundException
import com.fif.payments.seed.extensions.toIsoString
import com.fif.payments.seed.extensions.toSnakeCase
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ExceptionHandler(
        private val httpServletRequest: HttpServletRequest,
        private val tracer: Tracer?
) {

    private val log = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @ExceptionHandler(Throwable::class)
    fun handle(ex: Throwable): ResponseEntity<ApiErrorResponse> {
        val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        log.error(httpStatus.reasonPhrase, ex)
        val spError = SPError.INTERNAL_ERROR
        return buildResponseError(httpStatus, ex, spError.errorCode, spError.defaultMessage)
    }

    @ExceptionHandler(GenericException::class)
    fun handle(ex: GenericException): ResponseEntity<ApiErrorResponse> {
        log.error(HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, ex)
        return buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex, ex.errorCode)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handle(ex: ResourceNotFoundException): ResponseEntity<ApiErrorResponse> {
        log.error(HttpStatus.NOT_FOUND.reasonPhrase, ex)
        return buildResponseError(HttpStatus.NOT_FOUND, ex, ex.errorCode)
    }

    @ExceptionHandler(RestClientGenericException::class)
    fun handle(ex: RestClientGenericException): ResponseEntity<ApiErrorResponse> {
        log.error(HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, ex)
        return buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex, ex.errorCode)
    }

    @ExceptionHandler(TimeoutRestClientException::class)
    fun handle(ex: TimeoutRestClientException): ResponseEntity<ApiErrorResponse> {
        log.error(HttpStatus.GATEWAY_TIMEOUT.reasonPhrase, ex)
        return buildResponseError(HttpStatus.GATEWAY_TIMEOUT, ex, ex.errorCode)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException) = ex
            .also { log.error(HttpStatus.BAD_REQUEST.reasonPhrase, it) }
            .let {
                val message: String =
                        it.bindingResult.fieldErrors.joinToString { error ->
                            "${error.field.toSnakeCase()} is invalid: ${error.defaultMessage}"
                        }
                buildResponseError(HttpStatus.BAD_REQUEST, it, SPError.BAD_REQUEST.errorCode, message)
            }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex: HttpMessageNotReadableException) = ex
            .also { log.error(HttpStatus.BAD_REQUEST.reasonPhrase, it) }
            .let {
                val message: String = when (val cause = it.cause) {
                    is MissingKotlinParameterException -> "${cause.parameter.name.orEmpty().toSnakeCase()} cannot be null"
                    else -> "Content could not be read"
                }
                buildResponseError(HttpStatus.BAD_REQUEST, it, SPError.BAD_REQUEST.errorCode, message)
            }

    private fun buildResponseError(
            httpStatus: HttpStatus,
            ex: Throwable,
            errorCode: Int,
            errorMessage: String = ex.message ?: ""): ResponseEntity<ApiErrorResponse> {

        val traceId = tracer
                ?.currentSpan()
                ?.context()
                ?.traceIdString()
                ?: TraceSleuthInterceptor.TRACE_ID_NOT_EXISTS

        val spanId = tracer
                ?.currentSpan()
                ?.context()
                ?.spanIdString()
                ?: TraceSleuthInterceptor.SPAN_ID_NOT_EXISTS

        val apiErrorResponse = ApiErrorResponse(
                timestamp = LocalDateTime.now(ZoneOffset.UTC).toIsoString(),
                name = httpStatus.reasonPhrase,
                detail = errorMessage,
                status = httpStatus.value(),
                code = errorCode,
                resource = httpServletRequest.requestURI,
                metadata = Metadata(xB3TraceId = traceId, xB3SpanId = spanId)
        )

        return ResponseEntity(apiErrorResponse, httpStatus)
    }

    data class Metadata(
            @get:JsonProperty(value = "x_b3_trace_id") val xB3TraceId: String,
            @get:JsonProperty(value = "x_b3_span_id") val xB3SpanId: String
    )

    data class ApiErrorResponse(
            val name: String,
            val status: Int,
            val timestamp: String,
            val code: Int,
            val resource: String,
            val detail: String,
            val metadata: Metadata
    )

}
