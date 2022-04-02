/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.typealiaz

import tech.antibytes.kmock.Mock

typealias Alias0 = (Any) -> Unit
typealias Alias1 = (Any) -> Any

@Mock(Platform::class)
interface Platform {
    fun doSomething(
        arg0: Any,
        arg1: Alias0,
        arg2: Alias1
    ): Any
}
