package com.fif.payments.seed.shared

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class CompanionLogger {

    val logger: Logger = LoggerFactory.getLogger(javaClass.enclosingClass)

    inline fun <T> T.log(function: Logger.(asd: T) -> Unit): T =
        also { logger.function(it) }
}
