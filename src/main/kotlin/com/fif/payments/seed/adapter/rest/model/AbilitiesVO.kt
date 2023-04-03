package com.fif.payments.seed.adapter.rest.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fif.payments.seed.domain.Ability

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AbilitiesVO(
        val ability: AbilityVO
) {

    fun toAbilitiesDomain(): Ability {
        return Ability(name = ability.name)
    }
}
