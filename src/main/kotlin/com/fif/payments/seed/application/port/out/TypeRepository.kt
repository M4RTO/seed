package com.fif.payments.seed.application.port.out

import com.fif.payments.seed.domain.Type

interface TypeRepository {
    fun getType(typeName: String?): Type
}