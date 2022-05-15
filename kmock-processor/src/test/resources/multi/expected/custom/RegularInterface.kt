package multi

import multi.template.custom.Regular1
import multi.template.custom.SharedContractRegular
import multi.template.custom.nested.Regular3
import tech.antibytes.kmock.MockShared

@MockShared(
    "sharedTest",
    SharedMulti::class,
)
private interface SharedMulti : Regular1, SharedContractRegular.Regular2, Regular3
