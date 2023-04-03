package com.fif.payments.seed.adapter.rest

import com.fif.payments.seed.adapter.rest.model.PokemonVO
import com.fif.payments.seed.application.port.out.PokemonRepository
import com.fif.payments.seed.config.SPError
import com.fif.payments.seed.config.exception.ResourceNotFoundException
import com.fif.payments.seed.shared.CompanionLogger
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate

@Repository
class PokemonRestClientAdapter(private val restTemplate: RestTemplate) : PokemonRepository {

    override fun getPokemon(name: String) = name
            .log { info("PokemonWebService Request: {}", URL_POKEMON) }
            .let { restTemplate.getForObject(URL_POKEMON, PokemonVO::class.java, it) }
            ?.toPokemonDomain()
            ?.log { info("PokemonWebService Response: $it") }
            ?: throw ResourceNotFoundException(SPError.RESOURCE_NOT_FOUND.errorCode, "No se encontro el pokemon $name")

    companion object: CompanionLogger() {
        private const val URL_POKEMON = "https://pokeapi.co/api/v2/pokemon/{name}"
    }

}
