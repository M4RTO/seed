package com.fif.payments.seed.config

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.util.FileSize
import ch.qos.logback.core.util.InvocationGate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.io.File
import org.mockito.Mockito.mock

@DisplayName("SizeBasedTriggeringPolicy Test")
@ExtendWith(MockitoExtension::class)
class SizeBasedTriggeringPolicyTest {

    private val fileSize = FileSize(1024L)
    private val event: ILoggingEvent = mock(ILoggingEvent::class.java)

    @Test
    @DisplayName("When called too soon it should return false")
    fun testItIsTooSoon() {
        // given
        val invocationGate: InvocationGate = mock(InvocationGate::class.java)
        `when`(invocationGate.isTooSoon(anyLong())).thenReturn(true)
        val policy = SizeBasedTriggeringPolicy<Any>()
        policy.maxFileSize = fileSize
        policy.invocationGate = invocationGate
        val file = mock(File::class.java)

        // when
        val actual = policy.isTriggeringEvent(file, event)

        // then
        assertThat(actual).isFalse
    }

    @Test
    @DisplayName("When max file size has not been exceeded it should return false")
    fun testMaxSizeStillNotReached() {
        // given
        val invocationGate: InvocationGate = mock(InvocationGate::class.java)
        `when`(invocationGate.isTooSoon(anyLong())).thenReturn(false)
        val policy = SizeBasedTriggeringPolicy<Any>()
        policy.maxFileSize = fileSize
        policy.invocationGate = invocationGate
        val file = mock(File::class.java)
        `when`(file.length()).thenReturn(fileSize.size - 1L)

        // when
        val actual = policy.isTriggeringEvent(file, event)

        // then
        assertThat(actual).isFalse
    }

    @Test
    @DisplayName("When max file size has been exceeded it should return true")
    fun testMaxSizeExceeded() {
        // given
        val invocationGate: InvocationGate = mock(InvocationGate::class.java)
        `when`(invocationGate.isTooSoon(anyLong())).thenReturn(false)
        val policy = SizeBasedTriggeringPolicy<Any>()
        policy.maxFileSize = fileSize
        policy.invocationGate = invocationGate
        val file = mock(File::class.java)
        `when`(file.length()).thenReturn(fileSize.size + 1L)

        // when
        val actual = policy.isTriggeringEvent(file, event)

        // then
        assertThat(actual).isTrue
    }

    @Test
    @DisplayName("When the file has been deleted it should return true")
    fun testFileWasDeleted() {
        // given
        val invocationGate: InvocationGate = mock(InvocationGate::class.java)
        `when`(invocationGate.isTooSoon(anyLong())).thenReturn(false)
        val policy = SizeBasedTriggeringPolicy<Any>()
        policy.maxFileSize = fileSize
        policy.invocationGate = invocationGate
        val file = mock(File::class.java)
        `when`(file.length()).thenReturn(0L)
        `when`(file.exists()).thenReturn(false)

        // when
        val actual = policy.isTriggeringEvent(file, event)

        // then
        assertThat(actual).isTrue
    }

}
