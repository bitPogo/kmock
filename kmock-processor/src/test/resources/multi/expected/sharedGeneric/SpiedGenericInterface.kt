package multi

import kotlin.Any
import kotlin.Comparable
import multi.template.sharedGeneric.Generic1
import multi.template.sharedGeneric.GenericSharedContract
import multi.template.sharedGeneric.nested.Generic2
import tech.antibytes.kmock.MockShared

@MockShared(
    "sharedTest",
    SharedGenericMulti::class,
)
private interface SharedGenericMulti<KMockTypeParameter0 : Any, KMockTypeParameter1,
    KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4, KMockTypeParameter5> :
    Generic1<KMockTypeParameter0, KMockTypeParameter1>,
    Generic2<KMockTypeParameter2, KMockTypeParameter3>,
    GenericSharedContract.Generic3<KMockTypeParameter4, KMockTypeParameter5> where
KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
KMockTypeParameter3 : Any, KMockTypeParameter3 : Comparable<KMockTypeParameter3>
