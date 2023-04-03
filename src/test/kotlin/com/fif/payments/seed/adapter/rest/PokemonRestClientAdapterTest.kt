package com.fif.payments.seed.adapter.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fif.payments.seed.adapter.rest.model.*
import com.fif.payments.seed.config.SPError
import com.fif.payments.seed.config.TestConfig
import com.fif.payments.seed.config.exception.ResourceNotFoundException
import com.fif.payments.seed.domain.Ability
import com.fif.payments.seed.domain.Pokemon
import com.fif.payments.seed.domain.Type
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.*

@DisplayName("PokemonRestClient Adapter Test")
@Import(TestConfig::class)
@RestClientTest(PokemonRestClientAdapter::class)
class PokemonRestClientAdapterTest {

    @Autowired
    private val client: PokemonRestClientAdapter? = null

    @Autowired
    private val server: MockRestServiceServer? = null

    @Autowired
    private val objectMapper: ObjectMapper? = null

    @Test
    @DisplayName("when the getPokemon is called, the adapter must return a EmptyOrNullBodyRestClientException")
    fun tesGetPokemonEmptyOrNullBodyRestClientException() {

        //given
        server?.expect(requestTo(EXPECTED_URI))?.andRespond(withNoContent())

        //when
        val thrown = catchThrowable { client?.getPokemon("pikachu") }

        //then
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("No se encontro el pokemon pikachu")
    }

    @Test
    @DisplayName("when the getPokemon is called, the adapter must return a NotFoundRestClientException")
    fun testGetPokemonNotFoundRestClientException() {

        //given
        server?.expect(requestTo(EXPECTED_URI))?.andRespond(withStatus(HttpStatus.NOT_FOUND))

        //when
        val thrown = catchThrowable { client?.getPokemon("pikachu") }

        //then
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage(SPError.RESOURCE_NOT_FOUND.defaultMessage)
    }

    @Test
    @DisplayName("when the getPokemon is called, the adapter must return a Pokemon domain object")
    fun testGetPokemonNormalCase() {

        //given
        val expectedResponse: Pokemon = expectedDomainForMockedResponse
        val detailsString = objectMapper!!.writeValueAsString(mockedResponse)
        server?.expect(requestTo(EXPECTED_URI))
                ?.andRespond(withSuccess(detailsString, MediaType.APPLICATION_JSON))

        //when
        val currentResponse: Pokemon? = client?.getPokemon("pikachu")

        //then
        assertThat(currentResponse).isEqualTo(expectedResponse)
    }

    private val mockedResponse: PokemonVO
        get() {
            val typeModel = TypeVO("normal")
            val abilityModel = AbilityVO("thunder")
            val abilitiesModel = AbilitiesVO(abilityModel)
            val typesModel = TypesVO(typeModel)
            return PokemonVO(1, "pikachu", listOf(abilitiesModel), listOf(typesModel))
        }
    private val expectedDomainForMockedResponse: Pokemon
        get() {
            val ability = Ability("thunder")
            val type = Type("normal")

            return Pokemon("pikachu", ability, type)
        }

    companion object {
        private const val EXPECTED_URI = "https://pokeapi.co/api/v2/pokemon/pikachu"
    }
}
