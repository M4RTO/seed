package com.fif.payments.seed.application.port.out

import com.fif.payments.seed.domain.Ability

interface AbilityRepository {
    fun getAbility(name: String): Ability
}