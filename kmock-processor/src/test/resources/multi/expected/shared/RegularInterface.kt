package multi

import multi.template.shared.Regular1
import multi.template.shared.SharedContractRegular
import multi.template.shared.nested.Regular3
import tech.antibytes.kmock.MockShared

@MockShared(
    "sharedTest",
    SharedMulti::class,
)
private interface SharedMulti : Regular1, SharedContractRegular.Regular2, Regular3
