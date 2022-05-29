package multi

import multi.template.kmock.shared.Regular1
import multi.template.kmock.shared.SharedContractRegular
import multi.template.kmock.shared.nested.Regular3
import tech.antibytes.kmock.MockShared

@MockShared(
    "sharedTest",
    SharedMulti::class,
)
private interface SharedMulti : Regular1, SharedContractRegular.Regular2, Regular3
