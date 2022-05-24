@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import multi.template.platformGeneric.Generic1
import multi.template.platformGeneric.GenericPlatformContract
import multi.template.platformGeneric.nested.Generic2
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
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal inline fun <reified Mock : SpyOn, reified SpyOn> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector = NoopCollector,
    freeze: Boolean = true,
): Mock = getMockInstance(
    spyOn = spyOn,
    verifier = verifier,
    relaxed = false,
    relaxUnitFun = false,
    freeze = freeze,
)

private inline fun <reified Mock : SpyOn, reified SpyOn, KMockTypeParameter0 : Any,
    KMockTypeParameter1, KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4,
    KMockTypeParameter5> getMockInstance(
    spyOn: SpyOn?,
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType0: kotlin.reflect.KClass<multi.template.platformGeneric.Generic1<KMockTypeParameter0,
        KMockTypeParameter1>>,
    templateType1: kotlin.reflect.KClass<multi.template.platformGeneric.nested.Generic2<KMockTypeParameter2,
        KMockTypeParameter3>>,
    templateType2: kotlin.reflect.KClass<multi.template.platformGeneric.GenericPlatformContract.Generic3<KMockTypeParameter4,
        KMockTypeParameter5>>,
): Mock where SpyOn : Generic1<KMockTypeParameter0, KMockTypeParameter1>, SpyOn :
Generic2<KMockTypeParameter2, KMockTypeParameter3>, SpyOn :
              GenericPlatformContract.Generic3<KMockTypeParameter4, KMockTypeParameter5>, KMockTypeParameter1
              : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>, KMockTypeParameter3 : Any,
              KMockTypeParameter3 : Comparable<KMockTypeParameter3> = if (Mock::class ==
    multi.PlatformGenericMultiMock::class) {
    multi.PlatformGenericMultiMock(verifier = verifier, freeze = freeze, spyOn = spyOn as SpyOn?) as
        Mock
} else {
    throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")}

internal inline fun <reified Mock : SpyOn, reified SpyOn, KMockTypeParameter0 : Any,
    KMockTypeParameter1, KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4,
    KMockTypeParameter5> kspy(
    spyOn: SpyOn,
    collector: KMockContract.Collector = NoopCollector,
    freeze: Boolean = true,
    templateType0: kotlin.reflect.KClass<multi.template.platformGeneric.Generic1<KMockTypeParameter0,
        KMockTypeParameter1>>,
    templateType1: kotlin.reflect.KClass<multi.template.platformGeneric.nested.Generic2<KMockTypeParameter2,
        KMockTypeParameter3>>,
    templateType2: kotlin.reflect.KClass<multi.template.platformGeneric.GenericPlatformContract.Generic3<KMockTypeParameter4,
        KMockTypeParameter5>>,
): Mock where SpyOn : Generic1<KMockTypeParameter0, KMockTypeParameter1>, SpyOn :
Generic2<KMockTypeParameter2, KMockTypeParameter3>, SpyOn :
              GenericPlatformContract.Generic3<KMockTypeParameter4, KMockTypeParameter5>, KMockTypeParameter1
              : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>, KMockTypeParameter3 : Any,
              KMockTypeParameter3 : Comparable<KMockTypeParameter3> = getMockInstance(
    spyOn = spyOn,
    verifier = verifier,
    relaxed = false,
    relaxUnitFun = false,
    freeze = freeze,
    templateType0 = templateType0,
    templateType1 = templateType1,
    templateType2 = templateType2,
)
