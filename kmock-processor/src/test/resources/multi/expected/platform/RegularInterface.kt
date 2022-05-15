package multi

import multi.template.platform.PlatformContractRegular
import multi.template.platform.Regular1
import multi.template.platform.nested.Regular3
import tech.antibytes.kmock.Mock

@Mock(PlatformMulti::class)
private interface PlatformMulti : Regular1, PlatformContractRegular.Regular2, Regular3
