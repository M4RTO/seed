package com.fif.payments.seed.adapter.rest.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class EffectEntriesVO(
        val effect: String
)
