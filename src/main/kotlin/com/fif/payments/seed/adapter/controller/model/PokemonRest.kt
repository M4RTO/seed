package com.fif.payments.seed.adapter.controller.model

import com.fif.payments.seed.domain.Pokemon
import javax.validation.Valid
import javax.validation.constraints.NotBlank

data class PokemonRest(
        @get:NotBlank val name: String,
        @get:Valid val ability: AbilityRest,
        @get:Valid val type: TypeRest
) {

    fun toDomain() =
            Pokemon(name = name, ability = ability.toDomain())

    companion object {
        infix fun from(pokemon: Pokemon): PokemonRest {
            return PokemonRest(
                    name = pokemon.name,
                    ability = AbilityRest.toAbilityRest(pokemon.ability),
                    type = TypeRest.toTypeRest(pokemon.type))
        }
    }
}
