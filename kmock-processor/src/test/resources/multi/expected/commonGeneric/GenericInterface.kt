package multi

import kotlin.Any
import kotlin.Comparable
import multi.template.commonGeneric.Generic1
import multi.template.commonGeneric.GenericCommonContract
import multi.template.commonGeneric.nested.Generic2
import tech.antibytes.kmock.MockCommon

@MockCommon(CommonGenericMulti::class)
private interface CommonGenericMulti<KMockTypeParameter0 : Any, KMockTypeParameter1,
    KMockTypeParameter2 : Any, KMockTypeParameter3, KMockTypeParameter4, KMockTypeParameter5> :
    Generic1<KMockTypeParameter0, KMockTypeParameter1>,
    Generic2<KMockTypeParameter2, KMockTypeParameter3>,
    GenericCommonContract.Generic3<KMockTypeParameter4, KMockTypeParameter5> where
KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
KMockTypeParameter3 : Any, KMockTypeParameter3 : Comparable<KMockTypeParameter3>
