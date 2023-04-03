package com.fif.payments.seed.domain

import java.math.BigDecimal

data class Pokemon(
        val name: String,
        val ability: Ability? = null,
        val type: Type? = null,
        var health: BigDecimal? = null
) {

    fun attack(damage: BigDecimal) {
        health = health?.subtract(damage)
    }

}