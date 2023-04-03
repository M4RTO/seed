package com.fif.payments.seed.application.usecase

import com.fif.payments.seed.application.port.`in`.FindPokemonAbilityInPort
import com.fif.payments.seed.application.port.out.AbilityRepository
import com.fif.payments.seed.application.port.out.PokemonRepository
import com.fif.payments.seed.application.port.out.TypeRepository
import com.fif.payments.seed.domain.Pokemon
import com.fif.payments.seed.shared.CompanionLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

@Component
class GetPokemonAbilityUseCase(
        private val pokemonRepository: PokemonRepository,
        private val abilityRepository: AbilityRepository,
        private val typeRepository: TypeRepository,
        @Qualifier("asyncExecutor") private val executor: Executor
) : FindPokemonAbilityInPort {

    override fun by(name: String): CompletionStage<Pokemon> {
        val fPokemon = CompletableFuture.supplyAsync({ pokemonRepository.getPokemon(name) }, executor)
        fPokemon.thenAccept { log { info("Pokemon: {}", it) } }

        val fAbility = fPokemon.thenCompose { p ->
            CompletableFuture.supplyAsync({ abilityRepository.getAbility(p.ability!!.name) }, executor)
        }

        val fType = fPokemon.thenCompose { p ->
            CompletableFuture.supplyAsync({ typeRepository.getType(p.type?.name) }, executor)
        }

        return CompletableFuture.allOf(fAbility, fType).thenApply {
            val pokemon = fPokemon.join()
            val ability = fAbility.join()
            val type = fType.join()

            Pokemon(
                    ability = ability,
                    type = type,
                    name = pokemon.name
            )
        }
    }

    companion object: CompanionLogger()
}
