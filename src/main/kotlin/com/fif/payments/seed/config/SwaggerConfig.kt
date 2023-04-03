package com.fif.payments.seed.config

import com.fif.payments.seed.config.ExceptionHandler.ApiErrorResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.ResponseBuilder
import springfox.documentation.schema.Example
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import java.util.UUID

@Configuration
class SwaggerConfig {

    @Bean
    fun getDocket(): Docket {
        return Docket(DocumentationType.OAS_30)
            .forCodeGeneration(true)
            .useDefaultResponseMessages(false)
            .globalResponses(HttpMethod.GET, getDefaultErrorResponses())
            .globalResponses(HttpMethod.POST, getDefaultErrorResponses())
            .globalResponses(HttpMethod.PUT, getDefaultErrorResponses())
            .globalResponses(HttpMethod.PATCH, getDefaultErrorResponses())
            .globalResponses(HttpMethod.DELETE, getDefaultErrorResponses())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.fif.payments.seed.adapter.controller"))
            .paths(PathSelectors.any())
            .build()
    }

    private fun getDefaultErrorResponses() =
        listOf(
            HttpStatus.BAD_REQUEST,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.FORBIDDEN,
            HttpStatus.NOT_FOUND,
            HttpStatus.CONFLICT,
            HttpStatus.UNPROCESSABLE_ENTITY,
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatus.GATEWAY_TIMEOUT
        ).map {
            ResponseBuilder()
                .code(it.value().toString())
                .description(it.reasonPhrase)
                .isDefault(true)
                .examples(
                    listOf(
                        Example(
                            UUID.randomUUID().toString(),
                            it.reasonPhrase,
                            it.reasonPhrase,
                            apiErrorResponse.copy(status = it.value()),
                            "",
                            MediaType.APPLICATION_JSON.type
                        )
                    )
                )
                .build()
        }

    companion object {
        val apiErrorResponse = ApiErrorResponse(
            name = "String",
            status = 400,
            timestamp = "String",
            code = 100,
            resource = "String",
            detail = "String",
            metadata = ExceptionHandler.Metadata(
                "String",
                "String"
            )
        )
    }
}
