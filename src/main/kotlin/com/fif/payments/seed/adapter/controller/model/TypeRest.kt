package com.fif.payments.seed.adapter.controller.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fif.payments.seed.domain.Type

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TypeRest(
        val name: String?,
        val moveDamageClass: String? = null
) {

    companion object {
        fun toTypeRest(type: Type?): TypeRest {
            return TypeRest(
                    name = type?.name,
                    moveDamageClass = type?.moveDamageClass)
        }
    }
}
