package com.fif.payments.seed.adapter.rest.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fif.payments.seed.domain.Pokemon

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PokemonVO(
        val id: Long,
        val name: String,
        val abilities: List<AbilitiesVO>,
        val types: List<TypesVO>
) {

    fun toPokemonDomain(): Pokemon {
        return Pokemon(
                name = name,
                ability = abilities[0].toAbilitiesDomain(),
                type = types[0].toTypesDomain()
        )
    }
}
