package multi

import kotlin.Any
import kotlin.Comparable
import multi.template.platformGeneric.Generic1
import multi.template.platformGeneric.GenericPlatformContract
import multi.template.platformGeneric.nested.Generic2
import tech.antibytes.kmock.Mock

@Mock(PlatformGenericMulti::class)
private interface PlatformGenericMulti<KMockTypeParameter0 : Any, KMockTypeParameter1,
    KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4, KMockTypeParameter5> :
    Generic1<KMockTypeParameter0, KMockTypeParameter1>,
    Generic2<KMockTypeParameter2, KMockTypeParameter3>,
    GenericPlatformContract.Generic3<KMockTypeParameter4, KMockTypeParameter5> where
KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
KMockTypeParameter3 : Any, KMockTypeParameter3 : Comparable<KMockTypeParameter3>
