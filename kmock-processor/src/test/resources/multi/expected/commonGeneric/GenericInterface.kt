package multi

import kotlin.Any
import kotlin.Comparable
import multi.template.commonGeneric.Generic1
import multi.template.commonGeneric.GenericCommonContract
import multi.template.commonGeneric.nested.Generic2
import tech.antibytes.kmock.MockCommon

@MockCommon(CommonGenericMulti::class)
private interface CommonGenericMulti<T0 : Any, T1, T3 : Any, T4, T6, T7> : Generic1<T0, T1>,
    Generic2<T3, T4>, GenericCommonContract.Generic3<T6, T7> where T1 : Any, T1 : Comparable<T1>, T4
: Any, T4 : Comparable<T4>
