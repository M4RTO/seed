package com.fif.payments.seed.adapter.rest.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fif.payments.seed.domain.Type

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TypesVO(
        val type: TypeVO
) {

    fun toTypesDomain(): Type {
        return Type(name = type.name)
    }
}
