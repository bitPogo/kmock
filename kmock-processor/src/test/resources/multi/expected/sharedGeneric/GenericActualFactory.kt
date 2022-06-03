@file:Suppress("UNUSED_PARAMETER", "UNUSED_EXPRESSION")

package multi

import kotlin.Any
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Suppress
import kotlin.reflect.KClass
import multi.template.sharedGeneric.Generic1
import multi.template.sharedGeneric.GenericSharedContract
import multi.template.sharedGeneric.nested.Generic2
import tech.antibytes.kmock.KMockContract

private inline fun <reified Mock : SpyOn, reified SpyOn> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
): Mock = when (Mock::class) {
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
    KMockTypeParameter1, KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4,
    KMockTypeParameter5> getMockInstance(
    spyOn: SpyOn?,
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType0: KClass<Generic1<*, *>>,
    templateType1: KClass<Generic2<*, *>>,
    templateType2: KClass<GenericSharedContract.Generic3<*, *>>,
): Mock where SpyOn : Generic1<KMockTypeParameter0, KMockTypeParameter1>, SpyOn :
Generic2<KMockTypeParameter2, KMockTypeParameter3>, SpyOn :
              GenericSharedContract.Generic3<KMockTypeParameter4, KMockTypeParameter5>, KMockTypeParameter1 :
              Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>, KMockTypeParameter3 : Any,
              KMockTypeParameter3 : Comparable<KMockTypeParameter3> = multi.SharedGenericMultiMock(collector =
collector, freeze = freeze, spyOn = spyOn as SpyOn?) as Mock

internal actual inline fun <reified Mock :
SharedGenericMultiMock<KMockTypeParameter0, KMockTypeParameter1, KMockTypeParameter2, KMockTypeParameter3, KMockTypeParameter4, KMockTypeParameter5, *>,
    KMockTypeParameter0 : Any, KMockTypeParameter1, KMockTypeParameter2 : Any, KMockTypeParameter3,
    KMockTypeParameter4, KMockTypeParameter5> kmock(
    collector: KMockContract.Collector,
    relaxed: Boolean,
    relaxUnitFun: Boolean,
    freeze: Boolean,
    templateType0: KClass<Generic1<*, *>>,
    templateType1: KClass<Generic2<*, *>>,
    templateType2: KClass<GenericSharedContract.Generic3<*, *>>,
): Mock where KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
              KMockTypeParameter3 : Any, KMockTypeParameter3 : Comparable<KMockTypeParameter3> =
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
