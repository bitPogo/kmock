package multi

import multi.template.kmock.common.CommonContractRegular
import multi.template.kmock.common.Regular1
import multi.template.kmock.common.nested.Regular3
import tech.antibytes.kmock.MockCommon

@MockCommon(CommonMulti::class)
private interface CommonMulti : Regular1, CommonContractRegular.Regular2, Regular3
