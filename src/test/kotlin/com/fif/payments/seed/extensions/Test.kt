package com.fif.payments.seed.extensions

inline fun <R> Given(block: () -> R): R = block()
inline fun <T, R> T.When(block: (T) -> R) = block(this)
inline fun <T, R> T.Then(block: (T) -> R): R = block(this)
inline fun <T, R> T.And(block: (T) -> R): R = block(this)

infix fun String.`with no`(attributeName: String): String =
        if (stringAttributeRegex(attributeName).containsMatchIn(this))
            nullStringAttribute(this, attributeName)
        else
            nullNumberAttribute(this, attributeName)

fun String.`with another value`(attributeName: String, newValue: String): String =
        this.replace(stringAttributeRegex(attributeName), "\"$attributeName\": \"$newValue\"")

private fun stringAttributeRegex(attributeName: String) = "\"$attributeName\":\\s*\"[^\"]+?([^/\"]+)\"".toRegex()

private fun numberAttributeRegex(attributeName: String) = "\"$attributeName\":\\s*\\d+(\\.)?\\d*".toRegex()

private fun nullStringAttribute(json: String, attributeName: String) =
        json.replace(stringAttributeRegex(attributeName), "\"$attributeName\": null")

private fun nullNumberAttribute(json: String, attributeName: String) =
        json.replace(numberAttributeRegex(attributeName), "\"$attributeName\": null")
