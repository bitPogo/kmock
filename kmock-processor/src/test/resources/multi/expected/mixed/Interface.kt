package multi

import kotlin.Any
import kotlin.Comparable
import multi.template.mixed.Generic1
import multi.template.mixed.GenericPlatformContract
import multi.template.mixed.PlatformContractRegular
import multi.template.mixed.Regular1
import multi.template.mixed.nested.Generic2
import multi.template.mixed.nested.Regular3
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon

@MockCommon(CommonMulti::class)
private interface CommonMulti : Regular1, PlatformContractRegular.Regular2, Regular3

@Mock(PlatformMulti::class)
private interface PlatformMulti<KMockTypeParameter0 : Any, KMockTypeParameter1, KMockTypeParameter2,
    KMockTypeParameter3, KMockTypeParameter4 : Any, KMockTypeParameter5> :
    Generic1<KMockTypeParameter0, KMockTypeParameter1>,
    GenericPlatformContract.Generic3<KMockTypeParameter2, KMockTypeParameter3>,
    Generic2<KMockTypeParameter4, KMockTypeParameter5> where KMockTypeParameter1 : Any,
                                                             KMockTypeParameter1 : Comparable<KMockTypeParameter1>, KMockTypeParameter5 : Any,
                                                             KMockTypeParameter5 : Comparable<KMockTypeParameter5>
