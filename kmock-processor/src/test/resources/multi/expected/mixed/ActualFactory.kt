@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.reflect.KClass
import multi.template.mixed.Generic1
import multi.template.mixed.GenericPlatformContract
import multi.template.mixed.nested.Generic2
import tech.antibytes.kmock.KMockContract

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
    multi.CommonMultiMock::class -> multi.CommonMultiMock<multi.CommonMultiMock<*>>(collector =
    collector, relaxUnitFun = relaxUnitFun, freeze = freeze) as Mock
    else -> throw RuntimeException("Unknown Interface ${Mock::class.simpleName}.")
}

internal actual inline fun <reified Mock> kmock(
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = getMockInstance(
    spyOn = null,
    collector = collector,
    relaxed = relaxed,
    relaxUnitFun = relaxUnitFun,
    freeze = freeze,
)

private inline fun <reified Mock : SpyOn, reified SpyOn, KMockTypeParameter0 : Any,
    KMockTypeParameter1, KMockTypeParameter2, KMockTypeParameter3, KMockTypeParameter4 : Any,
    KMockTypeParameter5> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType0: KClass<Generic1<*, *>>,
    templateType1: KClass<GenericPlatformContract.Generic3<*, *>>,
    templateType2: KClass<Generic2<*, *>>,
): Mock where SpyOn : Generic1<KMockTypeParameter0, KMockTypeParameter1>, SpyOn :
GenericPlatformContract.Generic3<KMockTypeParameter2, KMockTypeParameter3>, SpyOn :
              Generic2<KMockTypeParameter4, KMockTypeParameter5>, KMockTypeParameter1 : Any,
              KMockTypeParameter1 : Comparable<KMockTypeParameter1>, KMockTypeParameter5 : Any,
              KMockTypeParameter5 : Comparable<KMockTypeParameter5> = multi.PlatformMultiMock(collector =
collector, freeze = freeze, spyOn = spyOn as SpyOn?) as Mock

internal inline fun <reified Mock :
PlatformMultiMock<KMockTypeParameter0, KMockTypeParameter1, KMockTypeParameter2, KMockTypeParameter3, KMockTypeParameter4, KMockTypeParameter5, *>,
    KMockTypeParameter0 : Any, KMockTypeParameter1, KMockTypeParameter2, KMockTypeParameter3,
    KMockTypeParameter4 : Any, KMockTypeParameter5> kmock(
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType0: KClass<Generic1<*, *>>,
    templateType1: KClass<GenericPlatformContract.Generic3<*, *>>,
    templateType2: KClass<Generic2<*, *>>,
): Mock where KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
              KMockTypeParameter5 : Any, KMockTypeParameter5 : Comparable<KMockTypeParameter5> =
    getMockInstance(
        spyOn = null,
        collector = collector,
        relaxed = relaxed,
        relaxUnitFun = relaxUnitFun,
        freeze = freeze,
        templateType0 = templateType0,
        templateType1 = templateType1,
        templateType2 = templateType2,
    )
