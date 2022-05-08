package multi

import multi.template.common.CommonContractRegular
import multi.template.common.Regular1
import multi.template.common.nested.Regular3
import tech.antibytes.kmock.MockCommon

@MockCommon(CommonMulti::class)
public interface CommonMulti : Regular1, CommonContractRegular.Regular2, Regular3
