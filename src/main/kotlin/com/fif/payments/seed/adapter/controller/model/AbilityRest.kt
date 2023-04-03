package com.fif.payments.seed.adapter.controller.model

import com.fif.payments.seed.domain.Ability
import java.math.BigDecimal
import javax.validation.constraints.Min
import javax.validation.constraints.Max

data class AbilityRest(
        val name: String?,
        val description: String? = null,
        @get:Min(0L) @get:Max(100L) val damage: BigDecimal? = null
) {

    fun toDomain(): Ability =
            Ability(name = name.orEmpty(), description = description, damage = damage)

    companion object {
        fun toAbilityRest(ability: Ability?): AbilityRest {
            return AbilityRest(
                    name = ability?.name,
                    description = ability?.description,
                    damage = ability?.damage
            )
        }
    }
}
