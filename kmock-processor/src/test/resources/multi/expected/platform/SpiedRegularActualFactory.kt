@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Boolean
import kotlin.Suppress
import multi.template.platform.PlatformContractRegular
import multi.template.platform.Regular1
import multi.template.platform.nested.Regular3
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.proxy.NoopCollector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    multi.PlatformMultiMock::class -> multi.PlatformMultiMock<multi.PlatformMultiMock<*>>(verifier =
    verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = true,
): Mock = getMockInstance(
    spyOn = null,
    verifier = verifier,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    verifier: KMockContract.Collector,
    freeze: Boolean,
    templateType0: kotlin.reflect.KClass<multi.template.platform.Regular1>,
    templateType1: kotlin.reflect.KClass<multi.template.platform.PlatformContractRegular.Regular2>,
    templateType2: kotlin.reflect.KClass<multi.template.platform.nested.Regular3>,
): Mock where SpyOn : Regular1, SpyOn : PlatformContractRegular.Regular2, SpyOn : Regular3 = if
                                                                                                 (Mock::class == multi.PlatformMultiMock::class) {
    multi.PlatformMultiMock(verifier = verifier, freeze = freeze, spyOn = spyOn as SpyOn?) as Mock
} else {
    throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")}
