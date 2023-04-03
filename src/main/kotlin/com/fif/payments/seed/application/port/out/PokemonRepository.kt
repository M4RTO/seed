package com.fif.payments.seed.application.port.out

import com.fif.payments.seed.domain.Pokemon

interface PokemonRepository {
    fun getPokemon(name: String): Pokemon
}