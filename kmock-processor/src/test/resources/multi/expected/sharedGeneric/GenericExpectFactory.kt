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
import tech.antibytes.kmock.proxy.NoopCollector

internal expect inline fun <reified Mock :
SharedGenericMultiMock<KMockTypeParameter0, KMockTypeParameter1, KMockTypeParameter2, KMockTypeParameter3, KMockTypeParameter4, KMockTypeParameter5, *>,
    KMockTypeParameter0 : Any, KMockTypeParameter1, KMockTypeParameter2 : Any, KMockTypeParameter3,
    KMockTypeParameter4, KMockTypeParameter5> kmock(
    collector: KMockContract.Collector = NoopCollector,
    relaxed: Boolean = false,
    relaxUnitFun: Boolean = false,
    freeze: Boolean = false,
    templateType0: KClass<Generic1<*, *>>,
    templateType1: KClass<Generic2<*, *>>,
    templateType2: KClass<GenericSharedContract.Generic3<*, *>>,
): Mock where KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
              KMockTypeParameter3 : Any, KMockTypeParameter3 : Comparable<KMockTypeParameter3>
