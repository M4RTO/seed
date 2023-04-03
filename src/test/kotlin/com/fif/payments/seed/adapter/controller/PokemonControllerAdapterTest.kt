package com.fif.payments.seed.adapter.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fif.payments.seed.adapter.controller.model.AbilityRest
import com.fif.payments.seed.adapter.controller.model.PokemonRest
import com.fif.payments.seed.adapter.controller.model.TypeRest
import com.fif.payments.seed.application.port.`in`.CreatePokemonInPort
import com.fif.payments.seed.application.port.`in`.FindPokemonAbilityInPort
import com.fif.payments.seed.config.SPError
import com.fif.payments.seed.config.TestConfig
import com.fif.payments.seed.config.exception.ResourceNotFoundException
import com.fif.payments.seed.domain.Ability
import com.fif.payments.seed.domain.Pokemon
import com.fif.payments.seed.domain.Type
import com.fif.payments.seed.extensions.`with no`
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID
import java.util.concurrent.CompletableFuture

@DisplayName("PokemonController Adapter Test")
@Import(TestConfig::class)
@WebMvcTest(PokemonControllerAdapter::class)
// Esto esta porque en la config por defecto registra el RestTemplateBuilder
@AutoConfigureWebClient(registerRestTemplate = true)
class PokemonControllerAdapterTest {

    @Autowired
    private val mockMvc: MockMvc? = null

    @Autowired
    private val objectMapper: ObjectMapper? = null

    @MockBean
    private val findPokemonAbilityInPort: FindPokemonAbilityInPort? = null

    @MockBean
    private val createPokemonInPort: CreatePokemonInPort? = null

    @Test
    @DisplayName("when the getPokemon is called, the adapter must return a pokemon")
    fun getPokemonOK() {
        `when`(findPokemonAbilityInPort!!.by(anyString())).thenReturn(CompletableFuture.completedFuture(POKEMON_DOMAIN))

        val result = mockMvc!!.get("/v1/pokemon/{name}", PIKACHU) {
            header("Authorization", "Bearer ${anyString()}")
        }.andReturn()

        mockMvc.perform(asyncDispatch(result))
                .andDo { print() }
                .andExpect {
                    status().isOk
                    content().json(objectMapper!!.writeValueAsString(POKEMON_REST))
                }

    }

    @Test
    @DisplayName("when the getPokemon is called, the adapter must return a NotFound exception")
    fun getPokemonNotFound() {
        `when`(findPokemonAbilityInPort!!.by(anyString()))
                .thenThrow(ResourceNotFoundException(SPError.RESOURCE_NOT_FOUND.errorCode,
                        SPError.RESOURCE_NOT_FOUND.defaultMessage))

        mockMvc!!.get("/v1/pokemon/{name}", PIKACHU) {
            header("Authorization", "Bearer ${anyString()}")
        }.andDo {
            print()
        }.andExpect {
            status().isNotFound
            content { string(containsString(SPError.RESOURCE_NOT_FOUND.defaultMessage)) }
        }

    }

    @Test
    @DisplayName("when the getPokemon is called, the adapter must return a NotFound async exception")
    fun getPokemonNotFoundAsync() {
        `when`(findPokemonAbilityInPort!!.by(anyString()))
                .thenReturn(CompletableFuture.failedFuture(
                        ResourceNotFoundException(SPError.RESOURCE_NOT_FOUND.errorCode,
                                SPError.RESOURCE_NOT_FOUND.defaultMessage))
                )

        val result = mockMvc!!.get("/v1/pokemon/{name}", PIKACHU) {
            header("Authorization", "Bearer ${anyString()}")
        }.andReturn()

        mockMvc.perform(asyncDispatch(result))
                .andDo { print() }
                .andExpect {
                    status().isNotFound
                    content().string(containsString(SPError.RESOURCE_NOT_FOUND.defaultMessage))
                }

    }

    @Test
    @DisplayName("when create is called, the adapter must return the created UUID")
    fun createPokemonOk() {
        val request = objectMapper!!.writeValueAsString(POKEMON_REST)
        val uuid = UUID.fromString("84f6523a-6793-40ef-a091-030178b3be0c")

        `when`(createPokemonInPort!!.execute(any())).thenReturn(uuid)

        mockMvc!!.post("/v1/pokemon") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isOk() }
            content { string(uuid.toString()) }
        }
    }

    @Test
    @DisplayName("when create is called with an empty body, the adapter must return a Bad Request")
    fun createPokemonEmptyBody() {
        mockMvc!!.post("/v1/pokemon") {
            contentType = MediaType.APPLICATION_JSON
            content = ""
        }.andExpect {
            status { isBadRequest() }
            content { json("{ \"code\": 103, \"detail\": \"Content could not be read\" }") }
        }
    }

    @Test
    @DisplayName("when create is called with a missing mandatory field, the adapter must return a Bad Request")
    fun createPokemonMissingMandatoryField() {
        val request = objectMapper!!.writeValueAsString(POKEMON_REST).`with no`("name")
        mockMvc!!.post("/v1/pokemon") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isBadRequest() }
            content { json("{ \"code\": 102, \"detail\": \"name cannot be null\" }") }
        }
    }

    @Test
    @DisplayName("when create is called with invalid fields, the adapter must return a Bad Request")
    fun createPokemonInvalidFields() {
        val invalidAbility = POKEMON_REST.ability.copy(damage = (-1).toBigDecimal())
        val request =
                objectMapper!!.writeValueAsString(POKEMON_REST.copy(ability = invalidAbility))

        mockMvc!!.post("/v1/pokemon") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isBadRequest() }
            content {
                json("{ \"code\": 103, \"detail\": " +
                        "\"ability.damage is invalid: must be greater than or equal to 0\" }")
            }
        }
    }

    companion object {
        private const val PIKACHU = "pikachu"

        private val POKEMON_DOMAIN = Pokemon(
                PIKACHU,
                Ability("thunder"),
                Type("normal")
        )

        private val POKEMON_REST = PokemonRest(
                PIKACHU,
                AbilityRest("thunder"),
                TypeRest("normal")
        )
    }

}
