@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Boolean
import kotlin.Suppress
import multi.template.custom.Regular1
import multi.template.custom.SharedContractRegular
import multi.template.custom.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    multi.SharedMultiMock::class -> multi.SharedMultiMock<multi.SharedMultiMock<*>>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock> kmock(
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = getMockInstance(
    spyOn = null,
    verifier = verifier,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)

internal actual inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    templateType0: kotlin.reflect.KClass<multi.template.custom.Regular1>,
    templateType1: kotlin.reflect.KClass<multi.template.custom.SharedContractRegular.Regular2>,
    templateType2: kotlin.reflect.KClass<multi.template.custom.nested.Regular3>,
): Mock where SpyOn : Regular1, SpyOn : SharedContractRegular.Regular2, SpyOn : Regular3 = if
                                                                                               (Mock::class == multi.SharedMultiMock::class) {
    multi.SharedMultiMock(verifier = verifier, freeze = freeze, spyOn = spyOn as SpyOn?) as Mock
} else {
    throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")}
