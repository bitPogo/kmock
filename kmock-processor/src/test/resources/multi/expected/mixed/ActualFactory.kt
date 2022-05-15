@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import multi.template.mixed.Generic1
import multi.template.mixed.GenericPlatformContract
import multi.template.mixed.nested.Generic2
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    multi.CommonMultiMock::class -> multi.CommonMultiMock<multi.CommonMultiMock<*>>(verifier =
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

private inline fun <reified Mock : SpyOn, reified SpyOn, KMockTypeParameter0 : Any,
    KMockTypeParameter1, KMockTypeParameter2, KMockTypeParameter3, KMockTypeParameter4 : Any,
    KMockTypeParameter5> getMockInstance(
    spyOn: SpyOn?,
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType0: kotlin.reflect.KClass<multi.template.mixed.Generic1<KMockTypeParameter0,
        KMockTypeParameter1>>,
    templateType1: kotlin.reflect.KClass<multi.template.mixed.GenericPlatformContract.Generic3<KMockTypeParameter2,
        KMockTypeParameter3>>,
    templateType2: kotlin.reflect.KClass<multi.template.mixed.nested.Generic2<KMockTypeParameter4,
        KMockTypeParameter5>>,
): Mock where SpyOn : Generic1<KMockTypeParameter0, KMockTypeParameter1>, SpyOn :
GenericPlatformContract.Generic3<KMockTypeParameter2, KMockTypeParameter3>, SpyOn :
              Generic2<KMockTypeParameter4, KMockTypeParameter5>, KMockTypeParameter1 : Any,
              KMockTypeParameter1 : Comparable<KMockTypeParameter1>, KMockTypeParameter5 : Any,
              KMockTypeParameter5 : Comparable<KMockTypeParameter5> = if (Mock::class ==
    multi.PlatformMultiMock::class) {
    multi.PlatformMultiMock(verifier = verifier, freeze = freeze, spyOn = spyOn as SpyOn?) as Mock
} else {
    throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")}

internal inline fun <reified Mock, KMockTypeParameter0 : Any, KMockTypeParameter1,
    KMockTypeParameter2, KMockTypeParameter3, KMockTypeParameter4 : Any, KMockTypeParameter5> kmock(
    verifier: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType0: kotlin.reflect.KClass<multi.template.mixed.Generic1<KMockTypeParameter0,
        KMockTypeParameter1>>,
    templateType1: kotlin.reflect.KClass<multi.template.mixed.GenericPlatformContract.Generic3<KMockTypeParameter2,
        KMockTypeParameter3>>,
    templateType2: kotlin.reflect.KClass<multi.template.mixed.nested.Generic2<KMockTypeParameter4,
        KMockTypeParameter5>>,
): Mock where Mock : Generic1<KMockTypeParameter0, KMockTypeParameter1>, Mock :
GenericPlatformContract.Generic3<KMockTypeParameter2, KMockTypeParameter3>, Mock :
              Generic2<KMockTypeParameter4, KMockTypeParameter5>, KMockTypeParameter1 : Any,
              KMockTypeParameter1 : Comparable<KMockTypeParameter1>, KMockTypeParameter5 : Any,
              KMockTypeParameter5 : Comparable<KMockTypeParameter5> = getMockInstance(
    spyOn = null,
    verifier = verifier,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
    templateType0 = templateType0,
    templateType1 = templateType1,
    templateType2 = templateType2,
)
