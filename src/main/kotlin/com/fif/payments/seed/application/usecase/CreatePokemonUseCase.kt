package com.fif.payments.seed.application.usecase

import com.fif.payments.seed.application.port.`in`.CreatePokemonInPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CreatePokemonUseCase : CreatePokemonInPort {

    private val log = LoggerFactory.getLogger(CreatePokemonUseCase::class.java)

    override fun execute(command: CreatePokemonInPort.Command): UUID {
        log.info("Pokemon a crear {}", command.pokemon)
        return UUID.randomUUID()
    }
}
