package test

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

internal expect inline fun <reified T> kmock(verifier: KMockContract.Collector = Collector { _, _ ->
    Unit }, relaxed: Boolean = false): T

internal expect inline fun <reified T> kspy(verifier: KMockContract.Collector = Collector { _, _ ->
    Unit }, spyOn: T): T
