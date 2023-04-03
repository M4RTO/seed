package com.fif.payments.seed.adapter.controller

import com.fif.payments.seed.adapter.controller.model.PokemonRest
import com.fif.payments.seed.application.port.`in`.CreatePokemonInPort
import com.fif.payments.seed.application.port.`in`.FindPokemonAbilityInPort
import com.fif.payments.seed.shared.CompanionLogger
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.*

@RestController
@RequestMapping("/v1/pokemon")
class PokemonControllerAdapter(
        private val findPokemonAbility: FindPokemonAbilityInPort,
        private val createPokemon: CreatePokemonInPort
) {

    @GetMapping("/{name}")
    fun find(@PathVariable("name") name: String): CompletionStage<PokemonRest> = name
            .log { info("Llamada al servicio /pokemon/future/{}", it) }
            .let { n ->
                findPokemonAbility.by(n)
                        .thenApply { p -> PokemonRest.from(p) }
                        .thenApply { response ->
                            response.log { info("Respuesta servicio getPokemon future: {}", it) }
                        }
            }

    @PostMapping
    fun create(@RequestBody @Validated req: PokemonRest): String = req
            .log { info("Llamada a creacion de pokemon {}", it) }
            .let { req.toDomain() }
            .let { CreatePokemonInPort.Command(it) }
            .let { createPokemon execute it }
            .toString()

    companion object: CompanionLogger()
}
