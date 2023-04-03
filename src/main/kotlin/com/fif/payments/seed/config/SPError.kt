package com.fif.payments.seed.config

enum class SPError(val errorCode: Int, val defaultMessage: String) {

    INTERNAL_ERROR(100, "Error interno del servidor"),
    RESOURCE_NOT_FOUND(101, "No se encontro recurso solicitado"),
    TIMEOUT_FOUND(102, "Se demoro demaciado tratando de realizar la accion solicitada"),
    BAD_REQUEST(103, "Bad Request al realizar la acción solicitada")

}
