package com.fif.payments.seed.archunit

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures

private const val DOMAIN = "Domain"
private const val ADAPTERS = "Adapters"
private const val APPLICATION = "Application"
private const val CONFIG = "Config"

@AnalyzeClasses(packages = ["com.fif.payments.seed"], importOptions = [DoNotIncludeTests::class])
class LayeredArchitectureTest {

    @ArchTest
    val layer_dependencies_are_respected: ArchRule = Architectures.layeredArchitecture()
            .layer(CONFIG).definedBy("com.fif.payments.seed.config..")
            .layer(DOMAIN).definedBy("com.fif.payments.seed.domain..")
            .layer(ADAPTERS).definedBy("com.fif.payments.seed.adapter..")
            .layer(APPLICATION).definedBy("com.fif.payments.seed.application..")
            .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, CONFIG)
            .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(CONFIG)
            .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, CONFIG)
}