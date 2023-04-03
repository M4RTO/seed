package com.fif.payments.seed.application.port.`in`

import com.fif.payments.seed.domain.Pokemon
import java.util.concurrent.CompletionStage

interface FindPokemonAbilityInPort {
    infix fun by(name: String): CompletionStage<Pokemon>
}
