/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.mixedannotation

import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.MockCommon

@MockCommon(Common::class)
@MockShared(
    "sharedTest",
    Shared::class
)
@Mock(Platform::class)
interface Platform {
    fun doSomething()
}

interface Shared {
    fun doSomething()
}

interface Common {
    fun doSomething()
}
