/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package factory.template.mixed

import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MockShared

@MockCommon(Common::class)
@MockShared(
    "sharedTest",
    Shared::class
)
@Mock(Platform::class)
interface Platform<P: Any> {
    val foo: P
}
