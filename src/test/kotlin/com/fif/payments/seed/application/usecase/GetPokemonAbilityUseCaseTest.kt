package com.fif.payments.seed.application.usecase

import com.fif.payments.seed.application.port.out.AbilityRepository
import com.fif.payments.seed.application.port.out.PokemonRepository
import com.fif.payments.seed.application.port.out.TypeRepository
import com.fif.payments.seed.domain.Ability
import com.fif.payments.seed.domain.Pokemon
import com.fif.payments.seed.domain.Type
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@DisplayName("Get PokemonAbility UseCase Test")
@ExtendWith(MockitoExtension::class)
class GetPokemonAbilityUseCaseTest {
    private val abilityRepository: AbilityRepository = mock(AbilityRepository::class.java)
    private val typeRepository: TypeRepository = mock(TypeRepository::class.java)
    private val pokemonRepository: PokemonRepository = mock(PokemonRepository::class.java)
    private val executor = ExecutorMockFactory.get()

    @Test
    @DisplayName("When GetPokemonAbilityUseCase is executed Should Return a Pokemon")
    fun testPokemonAbility() {

        //given
        val pokemonName = "pokemonName"
        val ability = Ability(name = "ability1", description = "description1", damage = BigDecimal.valueOf(1))
        val type = Type(name = "type1", moveDamageClass = "moveDamageClass1")
        val expected = Pokemon(name = "name1", ability = ability, type = type)
        `when`(abilityRepository.getAbility(anyString())).thenReturn(ability)
        `when`(typeRepository.getType(anyString())).thenReturn(type)
        `when`(pokemonRepository.getPokemon(anyString())).thenReturn(expected)
        val pokemonAbilityUseCase =
                GetPokemonAbilityUseCase(pokemonRepository, abilityRepository, typeRepository, executor)

        //when
        val pokemonViewActual: Pokemon = pokemonAbilityUseCase.by(pokemonName).toCompletableFuture().join()

        //then
        assertEquals(expected, pokemonViewActual)
    }

}
