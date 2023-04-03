package com.fif.payments.seed.application.port.`in`

import com.fif.payments.seed.domain.Pokemon
import java.util.UUID

interface CreatePokemonInPort {

    infix fun execute(command: Command): UUID

    data class Command(val pokemon: Pokemon)
}
