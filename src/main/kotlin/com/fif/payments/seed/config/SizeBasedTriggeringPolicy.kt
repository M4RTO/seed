package com.fif.payments.seed.config

import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy.DEFAULT_MAX_FILE_SIZE
import ch.qos.logback.core.rolling.TriggeringPolicyBase
import ch.qos.logback.core.util.DefaultInvocationGate
import ch.qos.logback.core.util.FileSize
import ch.qos.logback.core.util.InvocationGate
import java.io.File

class SizeBasedTriggeringPolicy<E>(
        var maxFileSize: FileSize = FileSize(DEFAULT_MAX_FILE_SIZE),
        var invocationGate: InvocationGate = DefaultInvocationGate()
) : TriggeringPolicyBase<E>() {

    override fun isTriggeringEvent(activeFile: File?, event: E): Boolean {
        val now = System.currentTimeMillis()
        if (invocationGate.isTooSoon(now)) {
            return false
        }
        val activeFileSizeInBytes = activeFile?.length() ?: 0L
        return (activeFileSizeInBytes >= maxFileSize.size ||
                (activeFileSizeInBytes == 0L && !fileExists(activeFile)))
    }

    private fun fileExists(activeFile: File?): Boolean {
        return (activeFile?.exists() ?: false)
    }

}