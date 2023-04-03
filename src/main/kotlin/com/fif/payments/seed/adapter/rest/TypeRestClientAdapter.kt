package com.fif.payments.seed.adapter.rest

import com.fif.payments.seed.adapter.rest.model.TypeVO
import com.fif.payments.seed.application.port.out.TypeRepository
import com.fif.payments.seed.config.SPError
import com.fif.payments.seed.config.exception.ResourceNotFoundException
import com.fif.payments.seed.shared.CompanionLogger
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate

@Repository
class TypeRestClientAdapter(private val restTemplate: RestTemplate) : TypeRepository {

    override fun getType(typeName: String?) = typeName
            .log { info("TypeWebService Request: {}", URL_API) }
            .let { restTemplate.getForObject(URL_API, TypeVO::class.java, it) }
            ?.toTypeDomain()
            ?.log { info("TypeWebService Response: $it") }
            ?: throw ResourceNotFoundException(SPError.RESOURCE_NOT_FOUND.errorCode, "No se encontro el tipo $typeName")

    companion object: CompanionLogger() {
        private const val URL_API = "https://pokeapi.co/api/v2/type/{type}"
    }

}
