package multi

import kotlin.Any
import kotlin.Comparable
import multi.template.commonGeneric.Generic1
import multi.template.commonGeneric.GenericCommonContract
import multi.template.commonGeneric.nested.Generic2
import tech.antibytes.kmock.MockCommon

@MockCommon(CommonGenericMulti::class)
private interface CommonGenericMulti<KMockTypeParameter0 : Any, KMockTypeParameter1,
    KMockTypeParameter3 : Any, KMockTypeParameter4, KMockTypeParameter6, KMockTypeParameter7> :
    Generic1<KMockTypeParameter0, KMockTypeParameter1>,
    Generic2<KMockTypeParameter3, KMockTypeParameter4>,
    GenericCommonContract.Generic3<KMockTypeParameter6, KMockTypeParameter7> where
KMockTypeParameter1 : Any, KMockTypeParameter1 : Comparable<KMockTypeParameter1>,
KMockTypeParameter4 : Any, KMockTypeParameter4 : Comparable<KMockTypeParameter4>
