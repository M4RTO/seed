package com.fif.payments.seed.adapter.rest

import com.fif.payments.seed.adapter.rest.model.AbilityVO
import com.fif.payments.seed.application.port.out.AbilityRepository
import com.fif.payments.seed.config.SPError
import com.fif.payments.seed.config.exception.ResourceNotFoundException
import com.fif.payments.seed.shared.CompanionLogger
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate

@Repository
class AbilityRestClientAdapter(
        private val restTemplate: RestTemplate
) : AbilityRepository {

    override fun getAbility(name: String) = name
            .log { info("AbilityWebService Request: {}", API_URL) }
            .let { restTemplate.getForObject(API_URL, AbilityVO::class.java, it) }
            ?.toAbilityDomain()
            ?.log { info("Llamado a ability por rest terminado, {}", it) }
            ?: throw ResourceNotFoundException(SPError.RESOURCE_NOT_FOUND.errorCode, "No se encontro la habilidad $name")

    companion object: CompanionLogger() {
        private const val API_URL = "https://pokeapi.co/api/v2/ability/{ability}"
    }

}
